package com.example.backend.admin.report.dto;

import com.example.backend.common.model.TicketPriority;

public record SlaBreachResponse(
    TicketPriority priority,
    long totalTickets,
    long breachedTickets,
    double breachRatePercent
) {
    
}
