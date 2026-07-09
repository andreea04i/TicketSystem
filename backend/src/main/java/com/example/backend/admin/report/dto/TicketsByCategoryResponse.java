package com.example.backend.admin.report.dto;

import com.example.backend.common.model.TicketCategory;

public record TicketsByCategoryResponse(
    TicketCategory category,
    long ticketCount
) {
    
}
