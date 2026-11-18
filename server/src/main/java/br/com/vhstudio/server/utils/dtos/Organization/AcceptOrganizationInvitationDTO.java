package br.com.vhstudio.server.utils.dtos.Organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AcceptOrganizationInvitationDTO(
        @NotBlank @Size(max = 200) String token) {}
