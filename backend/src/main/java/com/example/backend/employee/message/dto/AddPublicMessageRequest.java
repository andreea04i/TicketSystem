package com.example.backend.employee.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddPublicMessageRequest(

        @NotBlank(
                message = "Mesajul nu poate fi gol"
        )
        @Size(
                max = 5000,
                message = "Mesajul poate avea maximum 5000 de caractere"
        )
        String content
) {
}