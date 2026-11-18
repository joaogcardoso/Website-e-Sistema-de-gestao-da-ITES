package br.com.ites.server.controllers.Organization;

import br.com.ites.server.repositories.Organization.OrganizationRepository;
import br.com.ites.server.services.Organization.OrganizationFeatureService;
import br.com.ites.server.utils.dtos.Organization.OrganizationFeatureResponseDTO;
import br.com.ites.server.utils.dtos.Organization.OrganizationMembershipResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations")
@Tag(name = "Organizações", description = "Organizações e memberships da conta autenticada.")
@SecurityRequirement(name = "bearerAuth")
public class OrganizationController {
    private final OrganizationRepository organizations;
    private final OrganizationFeatureService features;

    public OrganizationController(OrganizationRepository organizations, OrganizationFeatureService features) {
        this.organizations = organizations;
        this.features = features;
    }

    @GetMapping
    public List<OrganizationMembershipResponseDTO> list(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return organizations.findMemberships(userId).stream()
                .map(membership -> new OrganizationMembershipResponseDTO(
                        membership.id(), membership.name(), membership.role(), membership.isDefault()))
                .toList();
    }

    @GetMapping("/{organizationId}/features")
    @Operation(summary = "Lista a disponibilidade das funcionalidades da organização",
            description = "Exige membership ativo; a resposta não concede permissões individuais.")
    public List<OrganizationFeatureResponseDTO> features(
            @AuthenticationPrincipal Jwt jwt, @PathVariable UUID organizationId) {
        return features.list(UUID.fromString(jwt.getSubject()), organizationId);
    }

}
