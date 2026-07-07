package com.example.backend.notification.controller;

import com.example.backend.notification.dto.NotificationResponse;
import com.example.backend.notification.dto.UnreadCountResponse;
import com.example.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> findOwnNotifications(
            Authentication authentication
    ) {
        return notificationService.findOwnNotifications(
                authentication
        );
    }

    @GetMapping("/unread-count")
    public UnreadCountResponse countUnread(
            Authentication authentication
    ) {
        return notificationService.countUnread(
                authentication
        );
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {
        return notificationService.markAsRead(
                notificationId,
                authentication
        );
    }

    @PatchMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllAsRead(
            Authentication authentication
    ) {
        notificationService.markAllAsRead(
                authentication
        );
    }
}