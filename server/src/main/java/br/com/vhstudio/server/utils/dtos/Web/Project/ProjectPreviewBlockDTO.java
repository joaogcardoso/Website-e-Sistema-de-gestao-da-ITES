package br.com.vhstudio.server.utils.dtos.Web.Project;

import tools.jackson.databind.JsonNode;

public record ProjectPreviewBlockDTO(String id, String definitionId, JsonNode props) {}
