package com.example.auth_security.common.mail.consumer;

import com.example.auth_security.common.mail.dto.RegisterMailDto;
import com.example.auth_security.common.mail.service.impl.MailServiceImpl;
import com.example.auth_security.core.config.RabbitMQConfig;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailQueueConsumer {

    private final MailServiceImpl mailService;
    @RabbitListener(queues = RabbitMQConfig.MAIL_QUEUE)
    public void consumeRegisterMail(RegisterMailDto dto) {
        mailService.sendRegisterMail(dto);
    }
}
