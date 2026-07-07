package com.example.backend.messages.dto;

import java.time.Instant;

public record MessageResponse(
        Long id,
        Long ticketId,
        Long authorId,
        String authorName,
        String content,
        boolean internal,
        Instant createdAt
) {
}