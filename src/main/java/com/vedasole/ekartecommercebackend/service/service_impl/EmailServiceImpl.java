package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.service.service_interface.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    /**
     * Sends a MIME message with HTML content using Thymeleaf template engine.
     *
     * @param to       the recipient email address
     * @param subject  the subject of the email
     * @param context  the context for the Thymeleaf template
     * @param template the name of the Thymeleaf template file (without extension)
     * @throws MessagingException if an error occurs while sending the email
     */
    @Override
    public void sendMimeMessage(String to, String subject, Context context, String template) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setFrom("no-reply.gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String html = templateEngine.process(template, context);
        helper.setText(html, true);
        emailSender.send(message);
        log.info("Email sent to email: '{}' with subject: '{}'", to, subject);
    }

}