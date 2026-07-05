package com.example.backend.ticket.dto;

import jakarta.validation.constraints.NotBlank;

public record EscalateTicketRequest (
    @NotBlank(message = "Motivul escaladarii este obligatoriu")
    String reason
){
}
