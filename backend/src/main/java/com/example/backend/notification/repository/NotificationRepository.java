package com.example.backend.notification.repository;

import com.example.backend.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    boolean existsByEventId(UUID eventId);

    List<Notification>
    findAllByRecipientIdOrderByCreatedAtDesc(
            Long recipientId
    );

    long countByRecipientIdAndReadFalse(
            Long recipientId
    );

    Optional<Notification>
    findByIdAndRecipientId(
            Long notificationId,
            Long recipientId
    );

    List<Notification>
    findAllByRecipientIdAndReadFalse(
            Long recipientId
    );
}