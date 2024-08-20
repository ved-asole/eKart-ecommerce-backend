package com.vedasole.ekartecommercebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private ApiKey apiKeys(){
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "Header");
    }

    private List<SecurityContext> securityContexts(){
        SecurityContext context = SecurityContext.builder()
                .securityReferences(securityReferences())
                .build();
        return List.of(context);
    }

    private List<SecurityReference> securityReferences() {
        AuthorizationScope authorizationScope = new AuthorizationScope(
                "global",
                "access everything"
        );
        return List.of(new SecurityReference(
                "JWT",
                new AuthorizationScope[]{ authorizationScope }));
    }

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .securityContexts(securityContexts())
                .securitySchemes(List.of(apiKeys()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.vedasole.ekartecommercebackend"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "Ekart E-commerce API",
                """
                        Ekart Ecommerce is an online e-commerce app which provides the facility for online shopping from any location.
                        A backend API project for the eKart E-commerce website developed using Java and Spring Framework.
                        
                        Author: Ved Asole(ved-asole)
                        
                        Project Link: https://github.com/ved-asole/eKart-ecommerce-backend
                        """,
                "1.0",
                "Terms of Service",
                new Contact(
                        "Ved Asole",
                        "https://www.vedasole.cloud",
                        "ekart-support@vedasole.cloud"
                ),
                "License of APIs",
                "https://github.com/ved-asole/eKart-ecommerce-backend/blob/master/LICENSE",
                Collections.emptyList()
        );
    }

}
