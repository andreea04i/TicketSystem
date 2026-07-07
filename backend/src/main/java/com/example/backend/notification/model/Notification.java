package com.example.backend.notification.model;

import com.example.backend.rabbitmq.TicketEventType;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.user.model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "notifications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_notifications_event_id",
                        columnNames = "event_id"
                )
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "event_id",
            nullable = false,
            updatable = false
    )
    private UUID eventId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User recipient;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "ticket_id",
            nullable = false
    )
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 50
    )
    private TicketEventType type;

    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            name = "message",
            nullable = false,
            length = 1000
    )
    private String message;

    @Column(
            name = "is_read",
            nullable = false
    )
    private boolean read;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    protected Notification() {
        // Constructor pentru JPA.
    }

    public static Notification create(
            UUID eventId,
            User recipient,
            Ticket ticket,
            TicketEventType type,
            String title,
            String message
    ) {
        Notification notification = new Notification();

        notification.eventId =
                Objects.requireNonNull(eventId);

        notification.recipient =
                Objects.requireNonNull(recipient);

        notification.ticket =
                Objects.requireNonNull(ticket);

        notification.type =
                Objects.requireNonNull(type);

        notification.title =
                Objects.requireNonNull(title);

        notification.message =
                Objects.requireNonNull(message);

        notification.read = false;

        return notification;
    }

    @PrePersist
    void beforeInsert() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void markAsRead() {
        this.read = true;
    }

    public Long getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public User getRecipient() {
        return recipient;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public TicketEventType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}