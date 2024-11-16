package com.vedasole.ekartecommercebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class TestMailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = mock(JavaMailSenderImpl.class);
        MimeMessage mimeMessage = new MimeMessage((javax.mail.Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        return mailSender;
    }
}