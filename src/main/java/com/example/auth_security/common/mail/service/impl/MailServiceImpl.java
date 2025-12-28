package com.example.auth_security.common.mail.service.impl;

import com.example.auth_security.common.mail.dto.RegisterMailDto;
import com.example.auth_security.common.mail.service.interfaces.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;



    @Override
    public void sendTextMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendRegisterMail(RegisterMailDto registerMailDto) {
        try {

            Context context = new Context();
            context.setVariable("username", registerMailDto.getUsername());
            context.setVariable("email", registerMailDto.getEmail());

            String html = templateEngine.process(
                    "mail/register-mail",
                    context
            );


            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(registerMailDto.getEmail());
            helper.setSubject("Welcome to Auth Security");
            helper.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Failed to send register mail to {}", registerMailDto.getEmail(), e);
            throw new IllegalStateException("Failed to send HTML email", e);
        }
    }

}
