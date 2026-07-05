package com.example.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Numele este obligatoriu")
        @Size(
                max = 100,
                message = "Numele poate avea maximum 100 de caractere"
        )
        String fullName,

        @NotBlank(message = "Emailul este obligatoriu")
        @Email(message = "Emailul nu este valid")
        @Size(
                max = 150,
                message = "Emailul poate avea maximum 150 de caractere"
        )
        String email,

        @NotBlank(message = "Parola este obligatorie")
        @Size(
                min = 8,
                max = 72,
                message = "Parola trebuie să aibă între 8 și 72 de caractere"
        )
        String password
) {
}