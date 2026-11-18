package br.com.vhstudio.server.utils.dtos.Web.Project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import tools.jackson.databind.JsonNode;

public record CreateProjectDTO(
        @NotBlank @Size(max = 150) String name, @NotNull JsonNode document) {}
