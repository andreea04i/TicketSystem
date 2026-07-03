package com.example.backend.sla.model;

import java.time.Instant;

import com.example.backend.common.model.TicketPriority;

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
@Table(name = "sla_config")

public class SlaConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, unique = true, length = 20)
    private TicketPriority priority;

    @Column(name = "resolution_hours", nullable = false)
    private int resolutionHours;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SlaConfig() {
        // Default constructor for JPA
    }

    public SlaConfig(TicketPriority priority, Integer resolutionHours) {
        this.priority = priority;
        this.resolutionHours = resolutionHours;
    }

    @PrePersist
    public void beforeInsert() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public Integer getResolutionHours() {
        return resolutionHours;
    }

    public void setResolutionHours(Integer resolutionHours) {
        this.resolutionHours = resolutionHours;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}