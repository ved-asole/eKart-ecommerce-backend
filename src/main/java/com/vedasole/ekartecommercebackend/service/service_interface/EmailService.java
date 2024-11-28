package com.vedasole.ekartecommercebackend.service.service_interface;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

@Service
public interface EmailService {

    void sendMimeMessage(String to, String subject, Context context, String template) throws MessagingException;

}