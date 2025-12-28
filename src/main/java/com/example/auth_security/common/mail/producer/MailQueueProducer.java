package com.example.auth_security.common.mail.producer;

import com.example.auth_security.common.mail.dto.RegisterMailDto;
import com.example.auth_security.core.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    public void enqueueRegisterMail(RegisterMailDto dto) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.MAIL_EXCHANGE,
            RabbitMQConfig.MAIL_ROUTING_KEY,
            dto
        );
    }

}
