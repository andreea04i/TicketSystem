package com.example.backend.notification.service;

import com.example.backend.notification.dto.NotificationResponse;
import com.example.backend.notification.dto.UnreadCountResponse;
import com.example.backend.notification.model.Notification;
import com.example.backend.notification.repository.NotificationRepository;
import com.example.backend.rabbitmq.TicketEvent;
import com.example.backend.rabbitmq.TicketEventType;
import com.example.backend.security.CurrentUserService;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.ticket.repository.TicketRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void createFromEvent(
            TicketEvent event
    ) {
        if (event == null || event.eventId() == null) {
            return;
        }

        if (event.recipientUserId() == null) {
            log.info(
                    "Evenimentul {} nu are destinatar",
                    event.eventId()
            );
            return;
        }

        if (Objects.equals(
                event.actorUserId(),
                event.recipientUserId()
        )) {
            return;
        }

        if (notificationRepository.existsByEventId(
                event.eventId()
        )) {
            return;
        }

        User recipient = userRepository
                .findById(event.recipientUserId())
                .orElse(null);

        Ticket ticket = ticketRepository
                .findById(event.ticketId())
                .orElse(null);

        if (recipient == null || ticket == null) {
            log.warn(
                    "Nu s-a putut crea notificarea pentru evenimentul {}",
                    event.eventId()
            );
            return;
        }

        Notification notification = Notification.create(
                event.eventId(),
                recipient,
                ticket,
                event.type(),
                buildTitle(event.type()),
                buildMessage(event)
        );

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> findOwnNotifications(
            Authentication authentication
    ) {
        User user =
                currentUserService.getCurrentUser(authentication);

        return notificationRepository
                .findAllByRecipientIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UnreadCountResponse countUnread(
            Authentication authentication
    ) {
        User user =
                currentUserService.getCurrentUser(authentication);

        long unreadCount =
                notificationRepository
                        .countByRecipientIdAndReadFalse(
                                user.getId()
                        );

        return new UnreadCountResponse(unreadCount);
    }

    @Transactional
    public NotificationResponse markAsRead(
            Long notificationId,
            Authentication authentication
    ) {
        User user =
                currentUserService.getCurrentUser(authentication);

        Notification notification =
                notificationRepository
                        .findByIdAndRecipientId(
                                notificationId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Notificarea nu a fost găsită"
                                )
                        );

        notification.markAsRead();

        return toResponse(notification);
    }

    @Transactional
    public void markAllAsRead(
            Authentication authentication
    ) {
        User user =
                currentUserService.getCurrentUser(authentication);

        List<Notification> notifications =
                notificationRepository
                        .findAllByRecipientIdAndReadFalse(
                                user.getId()
                        );

        notifications.forEach(
                Notification::markAsRead
        );
    }

    private String buildTitle(
            TicketEventType type
    ) {
        return switch (type) {
            case TICKET_CREATED ->
                    "Tichet nou";

            case MESSAGE_ADDED ->
                    "Mesaj nou";

            case STATUS_CHANGED ->
                    "Status schimbat";

            case TICKET_ASSIGNED ->
                    "Tichet atribuit";

            case TICKET_ESCALATED ->
                    "Tichet escaladat";
        };
    }

    private String buildMessage(
            TicketEvent event
    ) {
        String ticketNumber =
                event.ticketNumber() != null
                        ? event.ticketNumber()
                        : String.valueOf(event.ticketId());

        return switch (event.type()) {
            case TICKET_CREATED ->
                    "Tichetul " + ticketNumber
                            + " a fost creat.";

            case MESSAGE_ADDED ->
                    "A fost adăugat un mesaj nou la tichetul "
                            + ticketNumber + ".";

            case STATUS_CHANGED ->
                    "Statusul tichetului "
                            + ticketNumber
                            + " s-a schimbat din "
                            + safeValue(event.previousStatus())
                            + " în "
                            + safeValue(event.currentStatus())
                            + ".";

            case TICKET_ASSIGNED ->
                    "Tichetul " + ticketNumber
                            + " ți-a fost atribuit.";

            case TICKET_ESCALATED ->
                    "Tichetul " + ticketNumber
                            + " a fost escaladat.";
        };
    }

    private String safeValue(
            String value
    ) {
        return value != null ? value : "-";
    }

    private NotificationResponse toResponse(
            Notification notification
    ) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTicket().getId(),
                notification.getTicket().getTicketNumber(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}