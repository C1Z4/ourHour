package com.ourhour.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        String bearerAuthSchemeName = "bearerAuth";

        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement globalSecurityRequirement = new SecurityRequirement()
                .addList(bearerAuthSchemeName);

        return new OpenAPI()
                .info(new Info()
                        .title("OurHour API")
                        .description("OurHour Backend API 문서")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(bearerAuthSchemeName, bearerAuthScheme))
                .addSecurityItem(globalSecurityRequirement);
    }
}
