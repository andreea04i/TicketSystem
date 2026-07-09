package com.example.backend.admin.report.dto;

public record AverageResolutionTimeResponse(
    long resolvedTickets,
    double averageResolutionHours
) {
    
}
