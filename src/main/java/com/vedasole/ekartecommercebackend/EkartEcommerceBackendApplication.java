package com.vedasole.ekartecommercebackend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

/**
 * Main class of the E-Kart E-Commerce Backend application.
 * @author : Ved Asole
 */
@SpringBootApplication
@EnableCaching
@Slf4j
@RequiredArgsConstructor
public class EkartEcommerceBackendApplication {

    @Value("${spring.application.name:defaultAppName}")
    private String appName;

    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(EkartEcommerceBackendApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("EkartEcommerceBackendApplication started \uD83D\uDE80");
        log.info("Application name: {}, Port:{}", appName, environment.getProperty("local.server.port"));
    }

}