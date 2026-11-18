package br.com.vhstudio.server.controllers.Ites;

import br.com.vhstudio.server.services.Ites.ItesAccessService;
import tools.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ites/pages")
public class InstitutionalPageController {
    private static final Set<String> BLOCKS = Set.of("hero-classic", "features-classic", "contact-channels");
    private static final Set<String> PROPERTIES = Set.of("eyebrow", "title", "description", "button", "href");
    private final JdbcTemplate jdbc;
    private final ItesAccessService access;

    public InstitutionalPageController(JdbcTemplate jdbc, ItesAccessService access) {
        this.jdbc = jdbc; this.access = access;
    }

    public record DraftRequest(int revision, JsonNode document) {}
    public record PublishRequest(UUID versionId) {}

    @GetMapping
    public List<Map<String,Object>> list() {
        return jdbc.queryForList("SELECT id,slug,title FROM institutional_pages ORDER BY title");
    }

    @GetMapping("/{slug}")
    public Map<String,Object> published(@PathVariable String slug) {
        List<Map<String,Object>> rows = jdbc.queryForList("""
                SELECT p.id,p.slug,p.title,v.id AS version_id,v.revision,v.document::text AS document
                FROM institutional_pages p JOIN institutional_page_versions v ON v.id=p.published_version_id
                WHERE p.slug=?
                """,slug);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Página não publicada");
        return rows.getFirst();
    }

    @GetMapping("/{slug}/draft")
    public Map<String,Object> draft(@AuthenticationPrincipal Jwt jwt, @PathVariable String slug) {
        access.require(UUID.fromString(jwt.getSubject()), "builder.pages.edit");
        List<Map<String,Object>> rows = jdbc.queryForList("""
                SELECT p.id,p.slug,p.title,v.id AS version_id,v.revision,v.document::text AS document
                FROM institutional_pages p LEFT JOIN LATERAL (
                  SELECT id,revision,document FROM institutional_page_versions
                  WHERE page_id=p.id ORDER BY revision DESC LIMIT 1
                ) v ON true WHERE p.slug=?
                """,slug);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Página não encontrada");
        return rows.getFirst();
    }

    @PostMapping("/{slug}/draft")
    @Transactional
    public Map<String,Object> save(@AuthenticationPrincipal Jwt jwt, @PathVariable String slug,
                                   @RequestBody DraftRequest request) {
        UUID actor = UUID.fromString(jwt.getSubject());
        access.require(actor, "builder.pages.edit");
        validate(request.document());
        List<Map<String,Object>> pages = jdbc.queryForList("SELECT id FROM institutional_pages WHERE slug=? FOR UPDATE",slug);
        if (pages.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Página não encontrada");
        UUID pageId = (UUID)pages.getFirst().get("id");
        Integer current = jdbc.queryForObject("SELECT COALESCE(max(revision),0) FROM institutional_page_versions WHERE page_id=?",Integer.class,pageId);
        if (current == null || current != request.revision()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Rascunho alterado em outra sessão");
        UUID versionId = UUID.randomUUID();
        jdbc.update("""
                INSERT INTO institutional_page_versions(id,page_id,revision,document,author_id)
                VALUES(?,?,?,?::jsonb,?)
                """,versionId,pageId,current+1,request.document().toString(),actor);
        return Map.of("versionId",versionId,"revision",current+1);
    }

    @PostMapping("/{slug}/publish")
    @Transactional
    public void publish(@AuthenticationPrincipal Jwt jwt, @PathVariable String slug,
                        @RequestBody PublishRequest request) {
        UUID actor = UUID.fromString(jwt.getSubject());
        access.require(actor, "builder.pages.publish");
        int count = jdbc.update("""
                UPDATE institutional_pages p SET published_version_id=?
                WHERE p.slug=? AND EXISTS(SELECT 1 FROM institutional_page_versions v WHERE v.id=? AND v.page_id=p.id)
                """,request.versionId(),slug,request.versionId());
        if (count == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Versão inválida");
        jdbc.update("""
                INSERT INTO institutional_page_events(page_id,version_id,actor_id,action)
                SELECT id,?,?,'PUBLISH' FROM institutional_pages WHERE slug=?
                """,request.versionId(),actor,slug);
    }

    @DeleteMapping("/{slug}/published")
    @Transactional
    public void unpublish(@AuthenticationPrincipal Jwt jwt, @PathVariable String slug) {
        UUID actor = UUID.fromString(jwt.getSubject());
        access.require(actor, "builder.pages.publish");
        List<Map<String,Object>> pages = jdbc.queryForList("SELECT id,published_version_id FROM institutional_pages WHERE slug=? FOR UPDATE",slug);
        if (pages.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Página não encontrada");
        UUID pageId=(UUID)pages.getFirst().get("id");
        UUID versionId=(UUID)pages.getFirst().get("published_version_id");
        if (versionId == null) return;
        jdbc.update("UPDATE institutional_pages SET published_version_id=NULL WHERE id=?",pageId);
        jdbc.update("INSERT INTO institutional_page_events(page_id,version_id,actor_id,action) VALUES(?,?,?,'UNPUBLISH')",pageId,versionId,actor);
    }

    private void validate(JsonNode document) {
        if (document == null || !document.isObject() || document.toString().length() > 100_000) invalid();
        JsonNode pages = document.path("pages");
        if (!pages.isArray() || pages.size()!=1) invalid();
        JsonNode blocks = pages.get(0).path("blocks");
        if (!blocks.isArray() || blocks.size()>20) invalid();
        for (JsonNode block: blocks) {
            if (!BLOCKS.contains(block.path("definitionId").asText())) invalid();
            JsonNode props=block.path("props");
            if (!props.isObject()) invalid();
            var fields=props.properties().iterator();
            while(fields.hasNext()) {
                var field=fields.next();
                if (!PROPERTIES.contains(field.getKey()) || !field.getValue().isTextual() || field.getValue().asText().length()>2000) invalid();
                if (field.getKey().equals("href") && !field.getValue().asText().matches("(/[^\\s]*|#[^\\s]*|https://[^\\s]*)")) invalid();
            }
        }
    }

    private void invalid() { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Documento inválido"); }
}
