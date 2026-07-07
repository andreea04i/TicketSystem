package com.example.backend.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter
        .JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter
        .MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    TopicExchange ticketEventsExchange() {
        return new TopicExchange(
                RabbitMqConstants.TICKET_EVENTS_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    Queue ticketNotificationsQueue() {
        return new Queue(
                RabbitMqConstants.NOTIFICATION_QUEUE,
                true
        );
    }

    @Bean
    Binding ticketNotificationsBinding(
            Queue ticketNotificationsQueue,
            TopicExchange ticketEventsExchange
    ) {
        return BindingBuilder
                .bind(ticketNotificationsQueue)
                .to(ticketEventsExchange)
                .with(
                        RabbitMqConstants
                                .ALL_TICKET_EVENTS_ROUTING_KEY
                );
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter(
                "com.example.backend.rabbitmq"
        );
    }
}