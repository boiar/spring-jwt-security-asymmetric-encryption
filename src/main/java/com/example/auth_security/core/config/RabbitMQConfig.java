package com.example.auth_security.core.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String MAIL_QUEUE = "mail-queue";
    public static final String MAIL_EXCHANGE = "mail-exchange";

    public static final String MAIL_ROUTING_KEY = "mail-routing-key";

    @Bean
    public Queue mailQueue(){
        return new Queue(MAIL_QUEUE, true);
    }

    @Bean
    public DirectExchange mailExchange() {
        return new DirectExchange(MAIL_EXCHANGE);
    }

    @Bean
    public Binding mailBinding(Queue mailQueue, DirectExchange mailExchange) {
        return BindingBuilder.bind(mailQueue)
                .to(mailExchange)
                .with(MAIL_ROUTING_KEY);
    }


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
