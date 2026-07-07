package com.example.backend.rabbitmq;

public final class RabbitMqConstants {

    public static final String TICKET_EVENTS_EXCHANGE =
            "ticket.events.exchange";

    public static final String NOTIFICATION_QUEUE =
            "ticket.notifications.queue";

    public static final String ALL_TICKET_EVENTS_ROUTING_KEY =
            "ticket.#";

    public static final String TICKET_CREATED_ROUTING_KEY =
            "ticket.created";

    public static final String MESSAGE_ADDED_ROUTING_KEY =
            "ticket.message.added";

    public static final String STATUS_CHANGED_ROUTING_KEY =
            "ticket.status.changed";

    public static final String TICKET_ASSIGNED_ROUTING_KEY =
            "ticket.assigned";

    public static final String TICKET_ESCALATED_ROUTING_KEY =
            "ticket.escalated";

    private RabbitMqConstants() {
        // Clasa conține numai constante.
    }
}