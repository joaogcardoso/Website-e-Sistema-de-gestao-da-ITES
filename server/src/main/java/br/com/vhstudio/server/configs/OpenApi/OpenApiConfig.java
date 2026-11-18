package br.com.vhstudio.server.configs.OpenApi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "VH Studio API",
                        version = "0.0.1-SNAPSHOT",
                        description =
                                "API do builder VH Studio. Os endpoints privados usam access token JWT; "
                                        + "as rotas /auth/browser usam cookies HttpOnly e validação de origem."))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {}
