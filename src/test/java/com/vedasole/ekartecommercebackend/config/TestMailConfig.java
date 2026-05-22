package com.vedasole.ekartecommercebackend.config;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;

@Configuration
public class TestMailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl() {
            @Override
            public @NonNull MimeMessage createMimeMessage() {
                return new MimeMessage((Session) null);
            }

            @Override
            public @NonNull MimeMessage createMimeMessage(@NonNull InputStream contentStream) {
                return createMimeMessage();
            }

            @Override
            public void send(@NonNull MimeMessage... mimeMessages) {
                // no-op for tests
            }

            @Override
            public void send(@NonNull MimeMessagePreparator... mimeMessagePreparators) {
                // no-op for tests
            }

            @Override
            public void send(@NonNull SimpleMailMessage... simpleMessages) {
                // no-op for tests
            }
        };
    }
}