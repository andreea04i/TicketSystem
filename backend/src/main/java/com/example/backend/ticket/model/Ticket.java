package com.example.backend.ticket.model;

import java.time.Instant;

import com.example.backend.common.model.TicketCategory;
import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;


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
        length = 100
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

    @Column(name = "assigned_agent_id")
    private Long assignedAgentId;

    @Column(
        name = "escalation_reason",
        columnDefinition = "TEXT"
    )
    private String escalationReason;

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

    protected Ticket() {
        // Default constructor for JPA
    }

    @PrePersist
    void beforeInsert() {
        Instant now = Instant.now();
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

    public Long getAssignedAgentId() {
        return assignedAgentId;
    }

    public String getEscalationReason() {
        return escalationReason;
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

    public void assignTo(Long agentId) {
        this.assignedAgentId = agentId;

        if (this.status == TicketStatus.OPEN) {
            this.status = TicketStatus.IN_PROGRESS;
        }
    }

    public void changeStatus(TicketStatus newStatus) {
        this.status = newStatus;
    }

    public void escalate(String reason) {
        this.status = TicketStatus.ESCALATED;
        this.escalationReason = reason;
    }
}
