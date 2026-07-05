package com.example.backend.ticket.dto;

import java.time.Instant;

import com.example.backend.common.model.TicketCategory;
import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;

public record AgentTicketResponse (
    Long id,
    String ticketNumber,
    String title,
    TicketCategory category,
    TicketPriority priority,
    TicketStatus status,
    Long assignedAgentId,
    String escalationReason,
    boolean slaBreached,
    Instant createdAt,
    Instant updatedAt
) {
    
}
