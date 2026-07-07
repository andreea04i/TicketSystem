package com.example.backend.employee.ticket.service;

import com.example.backend.common.model.TicketStatus;
import com.example.backend.employee.ticket.dto.CreateTicketRequest;
import com.example.backend.employee.ticket.dto.EmployeeTicketResponse;
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
public class EmployeeTicketService {

    private final TicketRepository ticketRepository;

    private final CurrentUserService currentUserService;

    private final TicketNumberGenerator ticketNumberGenerator;

    private final TicketEventPublisher ticketEventPublisher;

    @Transactional
    public EmployeeTicketResponse create(
            CreateTicketRequest request,
            Authentication authentication
    ) {
        User employee =
                currentUserService.getCurrentUser(authentication);

        Ticket ticket = Ticket.create(
                ticketNumberGenerator.generate(),
                request.title().trim(),
                request.description().trim(),
                request.category(),
                request.priority(),
                employee
        );

        Ticket savedTicket =
                ticketRepository.save(ticket);

        ticketEventPublisher.publishTicketCreated(
                savedTicket,
                employee.getId()
        );

        return toResponse(savedTicket);
    }

    public List<EmployeeTicketResponse> findOwnTickets(
            Authentication authentication
    ) {
        User employee =
                currentUserService.getCurrentUser(authentication);

        return ticketRepository
                .findAllByCreatedByIdOrderByCreatedAtDesc(
                        employee.getId()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EmployeeTicketResponse findOwnTicket(
            Long ticketId,
            Authentication authentication
    ) {
        User employee =
                currentUserService.getCurrentUser(authentication);

        Ticket ticket = findOwnedTicket(
                ticketId,
                employee.getId()
        );

        return toResponse(ticket);
    }

    @Transactional
    public EmployeeTicketResponse closeOwnTicket(
            Long ticketId,
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
                    "Tichetul este deja închis"
            );
        }

        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Poți închide numai un tichet rezolvat"
            );
        }

        TicketStatus previousStatus =
                ticket.getStatus();

        ticket.changeStatus(TicketStatus.CLOSED);

        Ticket savedTicket =
                ticketRepository.save(ticket);
        Long recipientUserId =
                savedTicket.getAssignedAgent() == null
                        ? null
                        : savedTicket.getAssignedAgent().getId();

        ticketEventPublisher.publishStatusChanged(
                savedTicket,
                employee.getId(),
                recipientUserId,
                previousStatus
        );

        return toResponse(savedTicket);
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

    private EmployeeTicketResponse toResponse(
            Ticket ticket
    ) {
        return new EmployeeTicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.isSlaBreached(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getResolvedAt(),
                ticket.getClosedAt()
        );
    }
}