package com.example.backend.agent.message.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.agent.message.dto.AddAgentMessageRequest;
import com.example.backend.common.model.TicketStatus;
import com.example.backend.messages.dto.MessageResponse;
import com.example.backend.messages.model.Message;
import com.example.backend.messages.repository.MessageRepository;
import com.example.backend.rabbitmq.TicketEventPublisher;
import com.example.backend.security.CurrentUserService;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.ticket.repository.TicketRepository;
import com.example.backend.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentMessageService {

    private final MessageRepository messageRepository;

    private final TicketRepository ticketRepository;

    private final CurrentUserService currentUserService;

    private final TicketEventPublisher ticketEventPublisher;

    public List<MessageResponse> findAllMessagesForAgent(
            Long ticketId
    ) {
        findTicketOrThrow(ticketId);

        return messageRepository
                .findAllByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MessageResponse addMessageAsAgent(
            Long ticketId,
            AddAgentMessageRequest request,
            Authentication authentication
    ) {
        User agent =
                currentUserService.getCurrentUser(authentication);

        Ticket ticket = findTicketOrThrow(ticketId);

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nu poți adăuga mesaje într-un tichet închis"
            );
        }

        Message message = Message.create(
                ticket,
                agent,
                request.content(),
                request.internal()
        );

        Message savedMessage =
                messageRepository.save(message);

        if (!request.internal()) {
            ticketEventPublisher.publishMessageAdded(
                    ticket,
                    agent.getId(),
                    ticket.getCreatedBy().getId()
            );
        }

        return toResponse(savedMessage);
    }

    private Ticket findTicketOrThrow(Long ticketId) {
        return ticketRepository
                .findById(ticketId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tichetul nu a fost găsit"
                        )
                );
    }

    private MessageResponse toResponse(Message message) {
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