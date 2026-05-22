package com.vedasole.ekartecommercebackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 configuration (springdoc) with JWT Bearer security scheme.
 * Replaces the old springfox configuration and exposes OpenAPI UI at /swagger-ui.html (springdoc default).
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("Ekart E-commerce API")
                .version("1.0")
                .description("Ekart Ecommerce is an online e-commerce app which provides the facility for online shopping from any location.\nA backend API project for the eKart E-commerce website developed using Java and Spring Framework.\n\nAuthor: Ved Asole (ved-asole)\nProject Link: https://github.com/ved-asole/eKart-ecommerce-backend")
                .contact(new Contact().name("Ved Asole").url("https://www.vedasole.cloud").email("ekart-support@vedasole.cloud"))
                .license(new License().name("MIT").url("https://github.com/ved-asole/eKart-ecommerce-backend/blob/master/LICENSE"));

        SecurityScheme bearerScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        return new OpenAPI()
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, bearerScheme))
                .security(List.of(new SecurityRequirement().addList(SECURITY_SCHEME_NAME)))
                .info(info);
    }

}