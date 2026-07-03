package com.example.backend.sla.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateSlaConfigRequest(
    @NotNull(message = "Numarul de ore este obligatoriu")
    @Positive(message = "Numarul de ore trebuie sa fie mai mare decat zero")
    Integer resolutionHours
) {
    
}
