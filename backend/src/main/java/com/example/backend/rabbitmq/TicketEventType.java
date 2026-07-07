package com.example.backend.rabbitmq;

public enum TicketEventType {

    TICKET_CREATED,
    MESSAGE_ADDED,
    STATUS_CHANGED,
    TICKET_ASSIGNED,
    TICKET_ESCALATED
}