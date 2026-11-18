package br.com.ites.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Import({TestcontainersConfiguration.class, RecordingMailGateway.class})
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "auth.jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
    "auth.totp-encryption-key=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
    "auth.refresh-reuse-grace=PT0S"
})
class ItesFlowTests {
    private static final AtomicInteger IP_SEQUENCE = new AtomicInteger();
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired RecordingMailGateway.Inbox inbox;

    @Test
    void requesterDemandIsPrivateAndAdminDecisionIsAudited() throws Exception {
        String requester = register();
        String other = register();
        String admin = register();
        UUID adminId = userId(admin);
        UUID adminRole = jdbc.queryForObject("SELECT id FROM app_roles WHERE code='ADMINISTRATOR'", UUID.class);
        jdbc.update("INSERT INTO app_user_roles(user_id,role_id) VALUES(?,?)", adminId, adminRole);

        String created = mvc.perform(post("/ites/demands").header("Authorization", requester)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("capacity","OTHER","capacityOther","Moradora","service","Formação","description","Solicito oficina de gestão coletiva","urgency","HIGH"))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode demand = json.readTree(created);
        assertTrue(demand.get("code").asText().startsWith("ITES-"));
        assertEquals("Pendente", demand.get("status").asText());
        mvc.perform(get("/ites/demands/" + demand.get("id").asText()).header("Authorization", other))
                .andExpect(status().isNotFound());
        mvc.perform(get("/ites/demands/review").header("Authorization", requester))
                .andExpect(status().isForbidden());
        mvc.perform(post("/ites/demands/" + demand.get("id").asText() + "/decision").header("Authorization", requester)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("status","Aceita para atendimento","justification","Aprovada"))))
                .andExpect(status().isForbidden());
        mvc.perform(post("/ites/demands/" + demand.get("id").asText() + "/decision").header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("status","Aceita para atendimento","justification","A equipe prestará atendimento"))))
                .andExpect(status().isOk());
        assertEquals(1, jdbc.queryForObject("SELECT count(*) FROM demand_events WHERE demand_id=? AND actor_id=?", Integer.class,
                UUID.fromString(demand.get("id").asText()), adminId));
        String own = mvc.perform(get("/ites/demands").header("Authorization", requester)).andReturn().getResponse().getContentAsString();
        assertTrue(own.contains("Aceita para atendimento"));
    }

    @Test
    void institutionalPageRequiresGrantsAndPublishesSavedDocument() throws Exception {
        String editor = register();
        String admin = register();
        UUID adminId=userId(admin);
        UUID roleId=jdbc.queryForObject("SELECT id FROM app_roles WHERE code='ADMINISTRATOR'",UUID.class);
        jdbc.update("INSERT INTO app_user_roles(user_id,role_id) VALUES(?,?)",adminId,roleId);
        String document=json.writeValueAsString(Map.of("version",1,"name","Início","activePageId","page-1","pages",java.util.List.of(Map.of("id","page-1","name","Início","slug","inicio","blocks",java.util.List.of(Map.of("id","block-1","definitionId","hero-classic","props",Map.of("title","Portal ITES")))))));
        mvc.perform(post("/ites/pages/inicio/draft").header("Authorization",editor).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("revision",0,"document",json.readTree(document)))))
                .andExpect(status().isForbidden());
        String response=mvc.perform(post("/ites/pages/inicio/draft").header("Authorization",admin).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("revision",0,"document",json.readTree(document)))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode saved=json.readTree(response);
        mvc.perform(post("/ites/pages/inicio/draft").header("Authorization",editor).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("revision",0,"document",json.readTree(document)))))
                .andExpect(status().isForbidden());
        JsonNode reopened=json.readTree(mvc.perform(get("/ites/pages/inicio/draft").header("Authorization",admin))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        JsonNode restoredDocument=json.readTree(reopened.get("document").asText());
        assertEquals("Portal ITES",restoredDocument.at("/pages/0/blocks/0/props/title").asText());
        mvc.perform(post("/ites/pages/inicio/draft").header("Authorization",admin).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("revision",0,"document",json.readTree(document)))))
                .andExpect(status().isConflict());
        mvc.perform(post("/ites/pages/inicio/publish").header("Authorization",editor).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("versionId",saved.get("versionId").asText()))))
                .andExpect(status().isForbidden());
        mvc.perform(post("/ites/pages/inicio/publish").header("Authorization",admin).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("versionId",saved.get("versionId").asText()))))
                .andExpect(status().isOk());
        JsonNode published=json.readTree(mvc.perform(get("/ites/pages/inicio")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        JsonNode publishedDocument=json.readTree(published.get("document").asText());
        assertEquals("Portal ITES",publishedDocument.at("/pages/0/blocks/0/props/title").asText());
        mvc.perform(delete("/ites/pages/inicio/published").header("Authorization",admin)).andExpect(status().isOk());
        mvc.perform(get("/ites/pages/inicio")).andExpect(status().isNotFound());
    }

    @Test
    void administratorCreatesVerifiedRequesterWithoutEmailDelivery() throws Exception {
        String admin = register();
        String requester = register();
        UUID adminId = userId(admin);
        UUID adminRole = jdbc.queryForObject("SELECT id FROM app_roles WHERE code='ADMINISTRATOR'", UUID.class);
        jdbc.update("INSERT INTO app_user_roles(user_id,role_id) VALUES(?,?)", adminId, adminRole);

        String accountJson = json.writeValueAsString(Map.of(
                "firstName", "Nova", "lastName", "Solicitante", "email", "nova-solicitante@example.invalid",
                "password", "RequesterPass45!"));
        mvc.perform(post("/ites/users").header("Authorization", requester)
                        .contentType(MediaType.APPLICATION_JSON).content(accountJson))
                .andExpect(status().isForbidden());

        String createdBody = mvc.perform(post("/ites/users").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content(accountJson))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode created = json.readTree(createdBody);
        UUID createdId = UUID.fromString(created.get("id").asText());
        assertTrue(createdBody.contains("nova-solicitante@example.invalid"));
        assertFalse(createdBody.contains("RequesterPass45!"));
        assertTrue(jdbc.queryForObject("SELECT email_verified_at IS NOT NULL FROM users WHERE id=?", Boolean.class, createdId));
        assertTrue(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM app_user_roles ur JOIN app_roles r ON r.id=ur.role_id WHERE ur.user_id=? AND r.code='REQUESTER')", Boolean.class, createdId));

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email", "nova-solicitante@example.invalid", "password", "RequesterPass45!"))))
                .andExpect(status().isOk());
    }

    private String register() throws Exception {
        String email="ites-"+UUID.randomUUID()+"@example.invalid";
        int sequence=IP_SEQUENCE.incrementAndGet();
        String ip="127.0.3."+(sequence%250+1);
        mvc.perform(post("/auth/signup/start").with(request -> { request.setRemoteAddr(ip); return request; }).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("email",email)))).andExpect(status().isAccepted());
        String token=inbox.token(email,"Confirme seu e-mail");
        String body=json.writeValueAsString(Map.of("token",token,"firstName","Pessoa","lastName","Teste",
                "password","StrongPass1!","acceptTerms",true));
        JsonNode result=json.readTree(mvc.perform(post("/auth/signup/complete").with(request -> { request.setRemoteAddr(ip); return request; }).contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        return "Bearer "+result.get("accessToken").asText();
    }

    private UUID userId(String bearer) throws Exception {
        String token=bearer.substring("Bearer ".length());
        String profile=mvc.perform(get("/auth/me").header("Authorization",bearer)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(json.readTree(profile).get("id").asText());
    }
}
