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

@Configuration
public class SwaggerConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .name(AUTHORIZATION_HEADER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );
    }

    private Info getApiInfo() {
        return new Info()
                .title("Ekart E-commerce API")
                .description("""
                        Ekart Ecommerce is an online e-commerce app which provides the facility for online shopping from any location.
                        A backend API project for the eKart E-commerce website developed using Java and Spring Framework.
                        
                        Author: Ved Asole(ved-asole)
                        
                        Project Link: https://github.com/ved-asole/eKart-ecommerce-backend
                        """)
                .version("1.0")
                .termsOfService("Terms of Service")
                .contact(new Contact()
                        .name("Ved Asole")
                        .url("https://www.vedasole.cloud")
                        .email("ekart-support@vedasole.cloud")
                )
                .license(new License()
                        .name("License of APIs")
                        .url("https://github.com/ved-asole/eKart-ecommerce-backend/blob/master/LICENSE")
                );
    }
}
