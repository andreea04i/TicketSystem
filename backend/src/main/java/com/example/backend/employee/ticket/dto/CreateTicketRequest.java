package com.example.backend.employee.ticket.dto;

import com.example.backend.common.model.TicketCategory;
import com.example.backend.common.model.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(

        @NotBlank(message = "Titlul este obligatoriu")
        @Size(
                max = 180,
                message = "Titlul poate avea maximum 180 de caractere"
        )
        String title,

        @NotBlank(message = "Descrierea este obligatorie")
        @Size(
                max = 5000,
                message = "Descrierea poate avea maximum 5000 de caractere"
        )
        String description,

        @NotNull(message = "Categoria este obligatorie")
        TicketCategory category,

        @NotNull(message = "Prioritatea este obligatorie")
        TicketPriority priority
) {
}