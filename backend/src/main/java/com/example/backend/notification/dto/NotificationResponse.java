package com.example.backend.notification.dto;

import com.example.backend.rabbitmq.TicketEventType;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        Long ticketId,
        String ticketNumber,
        TicketEventType type,
        String title,
        String message,
        boolean read,
        Instant createdAt
) {
}