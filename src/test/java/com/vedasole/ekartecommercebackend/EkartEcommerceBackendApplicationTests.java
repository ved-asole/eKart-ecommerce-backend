package com.vedasole.ekartecommercebackend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EkartEcommerceBackendApplicationTests {

    @Autowired
    EkartEcommerceBackendApplication application;


    @Test
    void contextLoads() {
        assertNotNull(application);
    }

    @Test
    void whenCreatingModelMapper_thenNoExceptions() {
        // Act and Assert
        Assertions.assertDoesNotThrow(ModelMapper::new);
    }

}
