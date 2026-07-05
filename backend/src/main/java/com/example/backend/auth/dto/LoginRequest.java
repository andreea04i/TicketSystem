package com.example.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Emailul este obligatoriu")
        @Email(message = "Emailul nu este valid")
        String email,

        @NotBlank(message = "Parola este obligatorie")
        String password
) {
}