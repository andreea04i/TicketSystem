package com.example.backend.auth.dto;

import com.example.backend.user.model.Role;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String fullName,
        String email,
        Role role
) {
}