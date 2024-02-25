package com.vedasole.ekartecommercebackend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main class of the E-Kart E-Commerce Backend application.
 */
@SpringBootApplication
public class EkartEcommerceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EkartEcommerceBackendApplication.class, args);
    }

    /**
     * Creates a new instance of the ModelMapper class.
     *
     * @return a new ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
