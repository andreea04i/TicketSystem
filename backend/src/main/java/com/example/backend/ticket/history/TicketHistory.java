package com.example.backend.ticket.history;

import java.time.Instant;
import java.util.Objects;

import com.example.backend.common.model.TicketStatus;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_history")
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "changed_by_user_id", nullable = false)
    private User changedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", nullable = false, length = 30)
    private TicketStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private TicketStatus newStatus;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private Instant changedAt;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    protected TicketHistory() {
    }

    public static TicketHistory create(
            Ticket ticket,
            User changedBy,
            TicketStatus oldStatus,
            TicketStatus newStatus,
            String reason
    ) {
        TicketHistory history = new TicketHistory();

        history.ticket = Objects.requireNonNull(ticket);
        history.changedBy = Objects.requireNonNull(changedBy);
        history.oldStatus = Objects.requireNonNull(oldStatus);
        history.newStatus = Objects.requireNonNull(newStatus);
        history.reason = reason == null ? null : reason.trim();

        return history;
    }

    @PrePersist
    void beforeInsert() {
        if (changedAt == null) {
            changedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public TicketStatus getOldStatus() {
        return oldStatus;
    }

    public TicketStatus getNewStatus() {
        return newStatus;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public String getReason() {
        return reason;
    }
}