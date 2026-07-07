package com.example.backend.rabbitmq;

import com.example.backend.common.model.TicketStatus;
import com.example.backend.ticket.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TicketEventPublisher {

    private final AmqpTemplate amqpTemplate;

    public void publishTicketCreated(
            Ticket ticket,
            Long actorUserId
    ) {
        TicketEvent event = createEvent(
                TicketEventType.TICKET_CREATED,
                ticket,
                actorUserId,
                null,
                "Tichetul a fost creat",
                null,
                ticket.getStatus().name()
        );

        publish(
                RabbitMqConstants.TICKET_CREATED_ROUTING_KEY,
                event
        );
    }

    public void publishMessageAdded(
            Ticket ticket,
            Long actorUserId,
            Long recipientUserId
    ) {
        TicketEvent event = createEvent(
                TicketEventType.MESSAGE_ADDED,
                ticket,
                actorUserId,
                recipientUserId,
                "A fost adăugat un mesaj public",
                null,
                ticket.getStatus().name()
        );

        publish(
                RabbitMqConstants.MESSAGE_ADDED_ROUTING_KEY,
                event
        );
    }

    public void publishStatusChanged(
            Ticket ticket,
            Long actorUserId,
            Long recipientUserId,
            TicketStatus previousStatus
    ) {
        TicketEvent event = createEvent(
                TicketEventType.STATUS_CHANGED,
                ticket,
                actorUserId,
                recipientUserId,
                "Statusul tichetului a fost schimbat",
                previousStatus.name(),
                ticket.getStatus().name()
        );

        publish(
                RabbitMqConstants.STATUS_CHANGED_ROUTING_KEY,
                event
        );
    }

    public void publishTicketAssigned(
            Ticket ticket,
            Long actorUserId,
            Long recipientUserId
    ) {
        TicketEvent event = createEvent(
                TicketEventType.TICKET_ASSIGNED,
                ticket,
                actorUserId,
                recipientUserId,
                "Tichetul a fost atribuit",
                null,
                ticket.getStatus().name()
        );

        publish(
                RabbitMqConstants.TICKET_ASSIGNED_ROUTING_KEY,
                event
        );
    }

    public void publishTicketEscalated(
            Ticket ticket,
            Long actorUserId,
            Long recipientUserId
    ) {
        TicketEvent event = createEvent(
                TicketEventType.TICKET_ESCALATED,
                ticket,
                actorUserId,
                recipientUserId,
                "Tichetul a fost escaladat",
                null,
                ticket.getStatus().name()
        );

        publish(
                RabbitMqConstants.TICKET_ESCALATED_ROUTING_KEY,
                event
        );
    }

    private TicketEvent createEvent(
            TicketEventType type,
            Ticket ticket,
            Long actorUserId,
            Long recipientUserId,
            String description,
            String previousStatus,
            String currentStatus
    ) {
        return new TicketEvent(
                UUID.randomUUID(),
                type,
                ticket.getId(),
                ticket.getTicketNumber(),
                actorUserId,
                recipientUserId,
                description,
                previousStatus,
                currentStatus,
                Instant.now()
        );
    }

    private void publish(
            String routingKey,
            TicketEvent event
    ) {
        amqpTemplate.convertAndSend(
                RabbitMqConstants.TICKET_EVENTS_EXCHANGE,
                routingKey,
                event
        );
    }
}