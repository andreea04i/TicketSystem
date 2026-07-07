package com.example.backend.employee.message.service;

import com.example.backend.common.model.TicketStatus;
import com.example.backend.employee.message.dto
        .AddPublicMessageRequest;
import com.example.backend.messages.dto.MessageResponse;
import com.example.backend.messages.model.Message;
import com.example.backend.messages.repository.MessageRepository;
import com.example.backend.rabbitmq.TicketEventPublisher;
import com.example.backend.security.CurrentUserService;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.ticket.repository.TicketRepository;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeMessageService {

    private final MessageRepository messageRepository;

    private final TicketRepository ticketRepository;

    private final CurrentUserService currentUserService;

    private final TicketEventPublisher ticketEventPublisher;

    @Transactional
    public MessageResponse addPublicMessage(
            Long ticketId,
            AddPublicMessageRequest request,
            Authentication authentication
    ) {
        User employee =
                currentUserService.getCurrentUser(authentication);

        Ticket ticket = findOwnedTicket(
                ticketId,
                employee.getId()
        );

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nu poți adăuga mesaje într-un tichet închis"
            );
        }

        Message message = Message.create(
                ticket,
                employee,
                request.content(),
                false
        );

        Message savedMessage =
                messageRepository.save(message);

        Long recipientUserId =
        ticket.getAssignedAgent() == null
                ? null
                : ticket.getAssignedAgent().getId();

        ticketEventPublisher.publishMessageAdded(
                ticket,
                employee.getId(),
                recipientUserId
        );
        return toResponse(savedMessage);
    }

    public List<MessageResponse> findPublicMessages(
            Long ticketId,
            Authentication authentication
    ) {
        User employee =
                currentUserService.getCurrentUser(authentication);

        findOwnedTicket(
                ticketId,
                employee.getId()
        );

        return messageRepository
                .findAllByTicketIdAndInternalFalseOrderByCreatedAtAsc(
                        ticketId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Ticket findOwnedTicket(
            Long ticketId,
            Long employeeId
    ) {
        return ticketRepository
                .findByIdAndCreatedById(
                        ticketId,
                        employeeId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tichetul nu a fost găsit"
                        )
                );
    }

    private MessageResponse toResponse(
            Message message
    ) {
        return new MessageResponse(
                message.getId(),
                message.getTicket().getId(),
                message.getAuthor().getId(),
                message.getAuthor().getFullName(),
                message.getContent(),
                message.isInternal(),
                message.getCreatedAt()
        );
    }
}