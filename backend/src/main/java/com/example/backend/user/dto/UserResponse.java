package com.example.backend.user.dto;

import com.example.backend.user.model.Role;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role
) {
}