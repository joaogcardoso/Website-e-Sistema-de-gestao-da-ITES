package br.com.ites.server.controllers.Ites;

import br.com.ites.server.services.Ites.ItesAccessService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ites/users")
public class ItesUserController {
    private final JdbcTemplate jdbc;
    private final ItesAccessService access;
    public ItesUserController(JdbcTemplate jdbc, ItesAccessService access) { this.jdbc=jdbc; this.access=access; }
    @GetMapping
    public List<Map<String,Object>> list(@AuthenticationPrincipal Jwt jwt) {
        access.require(UUID.fromString(jwt.getSubject()), "users.read");
        return jdbc.queryForList("SELECT id,first_name,last_name,email,active FROM users ORDER BY created_at DESC LIMIT 100");
    }
}
