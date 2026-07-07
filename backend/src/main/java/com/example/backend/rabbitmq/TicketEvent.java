package com.example.backend.rabbitmq;

import java.time.Instant;
import java.util.UUID;

public record TicketEvent(

        UUID eventId,

        TicketEventType type,

        Long ticketId,

        String ticketNumber,

        Long actorUserId,

        Long recipientUserId,

        String description,

        String previousStatus,

        String currentStatus,

        Instant occurredAt
) {
}