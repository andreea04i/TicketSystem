package com.example.backend.sla.dto;

import com.example.backend.common.model.TicketPriority;

import java.time.Instant;

public record SlaConfigResponse(
    Long id,
    TicketPriority priority,
    Integer resolutionHours,
    Instant updatedAt){
        
    }
