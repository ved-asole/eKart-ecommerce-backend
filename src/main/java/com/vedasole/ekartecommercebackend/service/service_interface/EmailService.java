package com.vedasole.ekartecommercebackend.service.service_interface;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public interface EmailService {

    void sendMimeMessage(String to, String subject, Context context, String template) throws MessagingException;

}