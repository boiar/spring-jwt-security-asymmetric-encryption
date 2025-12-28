package com.example.auth_security.common.mail.service.interfaces;

import com.example.auth_security.common.mail.dto.RegisterMailDto;

public interface MailService {

    void sendTextMail(String to, String subject, String body);

    void sendRegisterMail(RegisterMailDto mailDto);
}
