package com.example.backend.ticket.dto;

import jakarta.validation.constraints.NotNull;

public record AssignTicketRequest (
    @NotNull(message = "ID-ul agentului este obligatoriu")
    Long agentId ) {

    }
