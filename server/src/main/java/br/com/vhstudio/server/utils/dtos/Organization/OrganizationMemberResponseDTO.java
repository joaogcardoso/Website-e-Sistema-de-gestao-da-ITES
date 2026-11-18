package br.com.vhstudio.server.utils.dtos.Organization;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrganizationMemberResponseDTO(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String role,
        String state,
        OffsetDateTime createdAt) {}
