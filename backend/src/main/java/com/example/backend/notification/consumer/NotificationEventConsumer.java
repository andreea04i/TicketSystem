package com.example.backend.notification.consumer;

import com.example.backend.notification.service.NotificationService;
import com.example.backend.rabbitmq.RabbitMqConstants;
import com.example.backend.rabbitmq.TicketEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(
            queues = RabbitMqConstants.NOTIFICATION_QUEUE
    )
    public void consume(
            TicketEvent event
    ) {
        notificationService.createFromEvent(event);
    }
}