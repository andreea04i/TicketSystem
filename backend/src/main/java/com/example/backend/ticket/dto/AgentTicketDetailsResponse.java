package com.example.backend.ticket.dto;

import java.time.Instant;

import com.example.backend.common.model.TicketCategory;
import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;

public record AgentTicketDetailsResponse(
    Long id,
    String ticketNumber,
    String title,
    String description,

    TicketCategory category,
    TicketPriority priority,
    TicketStatus status,

    Long createdById,
    String createdByName,
    String createdByEmail,

    Long assignedAgentId,
    String assignedAgentName,
    String assignedAgentEmail,

    String escalationReason,
    boolean slaBreached,

    Instant createdAt,
    Instant updatedAt,
    Instant resolvedAt,
    Instant closedAt
) {
}
