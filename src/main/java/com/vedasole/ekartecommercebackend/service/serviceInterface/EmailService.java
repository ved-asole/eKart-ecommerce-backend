package com.vedasole.ekartecommercebackend.service.serviceInterface;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

@Service
public interface EmailService {

    void sendMimeMessage(String to, String subject, Context context, String template) throws MessagingException;

}