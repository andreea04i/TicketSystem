package com.example.backend.employee.ticket.dto;

import com.example.backend.common.model.TicketCategory;
import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;

import java.time.Instant;

public record EmployeeTicketResponse(
        Long id,
        String ticketNumber,
        String title,
        String description,
        TicketCategory category,
        TicketPriority priority,
        TicketStatus status,
        boolean slaBreached,
        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt,
        Instant closedAt
) {
}