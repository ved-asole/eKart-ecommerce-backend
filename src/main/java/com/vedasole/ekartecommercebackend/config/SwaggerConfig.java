package com.vedasole.ekartecommercebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.vedasole.ekartecommercebackend"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "Ekart E-commerce API",
                "This project is developed by Ved Asole(ved-asole)",
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
