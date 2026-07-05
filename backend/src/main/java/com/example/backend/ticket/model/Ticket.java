package com.example.backend.ticket.model;

import com.example.backend.common.model.TicketCategory;
import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;
import com.example.backend.user.model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "ticket_number",
            nullable = false,
            unique = true,
            length = 20
    )
    private String ticketNumber;

    @Column(
            name = "title",
            nullable = false,
            length = 180
    )
    private String title;

    @Column(
            name = "description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "category",
            nullable = false,
            length = 30
    )
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "priority",
            nullable = false,
            length = 20
    )
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private TicketStatus status;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "created_by_id",
            nullable = false
    )
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private User assignedAgent;

    @Column(
            name = "sla_breached",
            nullable = false
    )
    private boolean slaBreached;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    protected Ticket() {
        // Constructor necesar pentru JPA.
    }

    public static Ticket create(
            String ticketNumber,
            String title,
            String description,
            TicketCategory category,
            TicketPriority priority,
            User createdBy
    ) {
        Ticket ticket = new Ticket();

        ticket.ticketNumber =
                Objects.requireNonNull(ticketNumber);

        ticket.title =
                Objects.requireNonNull(title);

        ticket.description =
                Objects.requireNonNull(description);

        ticket.category =
                Objects.requireNonNull(category);

        ticket.priority =
                Objects.requireNonNull(priority);

        ticket.createdBy =
                Objects.requireNonNull(createdBy);

        ticket.status = TicketStatus.OPEN;
        ticket.slaBreached = false;

        return ticket;
    }

    public void changeStatus(TicketStatus newStatus) {
        status = Objects.requireNonNull(newStatus);

        Instant now = Instant.now();

        if (newStatus == TicketStatus.RESOLVED
                && resolvedAt == null) {
            resolvedAt = now;
        }

        if (newStatus == TicketStatus.CLOSED
                && closedAt == null) {
            closedAt = now;
        }
    }

    public void assignTo(User agent) {
        assignedAgent = Objects.requireNonNull(agent);
    }

    @PrePersist
    void beforeInsert() {
        Instant now = Instant.now();

        if (status == null) {
            status = TicketStatus.OPEN;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void beforeUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public User getAssignedAgent() {
        return assignedAgent;
    }

    public boolean isSlaBreached() {
        return slaBreached;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }
}