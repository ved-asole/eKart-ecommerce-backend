package com.vedasole.ekartecommercebackend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

/**
 * Main class of the E-Kart E-Commerce Backend application.
 * @author : Ved Asole
 */
@SpringBootApplication
@EnableCaching
public class EkartEcommerceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EkartEcommerceBackendApplication.class, args);
    }

    /**
     * Creates a new instance of the MapperConfig class.
     *
     * @return a new MapperConfig instance
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }
}
