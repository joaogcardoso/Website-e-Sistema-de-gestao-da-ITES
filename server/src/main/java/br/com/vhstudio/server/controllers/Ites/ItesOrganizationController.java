package br.com.vhstudio.server.controllers.Ites;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ites/organizations")
public class ItesOrganizationController {
    private final JdbcTemplate jdbc;
    public ItesOrganizationController(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public record NewOrganization(String name, String economicSector) {}

    @GetMapping
    public List<Map<String,Object>> mine(@AuthenticationPrincipal Jwt jwt) {
        return jdbc.queryForList("""
            SELECT o.id,o.name,o.economic_sector FROM organizations o
            JOIN organization_memberships m ON m.organization_id=o.id
            WHERE m.user_id=? ORDER BY o.name
            """, UUID.fromString(jwt.getSubject()));
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> create(@AuthenticationPrincipal Jwt jwt, @RequestBody NewOrganization request) {
        if (request == null || request.name() == null || request.name().isBlank() || request.name().length() > 150
                || request.economicSector() == null || request.economicSector().isBlank() || request.economicSector().length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da organização inválidos");
        }
        UUID userId = UUID.fromString(jwt.getSubject());
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO organizations(id,name,created_at,economic_sector) VALUES(?,?,now(),?)",
                id, request.name(), request.economicSector());
        jdbc.update("INSERT INTO organization_memberships(organization_id,user_id,role,is_default,created_at) VALUES(?,?,'MEMBER',false,now())",
                id, userId);
        return jdbc.queryForMap("SELECT id,name,economic_sector FROM organizations WHERE id=?", id);
    }
}
