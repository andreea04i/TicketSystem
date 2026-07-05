package com.example.backend.ticket.dto;

import com.example.backend.common.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeTicketStatusRequest (
    @NotNull(message = "Statusul este obligatoriu")
    TicketStatus status ){
}
