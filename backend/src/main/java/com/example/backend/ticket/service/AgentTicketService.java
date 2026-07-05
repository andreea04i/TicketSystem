package com.example.backend.ticket.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;
import com.example.backend.ticket.dto.AgentTicketResponse;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.ticket.repository.TicketRepository;
import com.example.backend.user.model.Role;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AgentTicketService {

    private static final List<TicketStatus> ACTIVE_STATUSES = List.of(
            TicketStatus.OPEN,
            TicketStatus.IN_PROGRESS,
            TicketStatus.ESCALATED
    );

    private static final Map<TicketPriority, Integer> PRIORITY_ORDER = Map.of(
            TicketPriority.CRITICAL, 1,
            TicketPriority.HIGH, 2,
            TicketPriority.MEDIUM, 3,
            TicketPriority.LOW, 4
    );

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public AgentTicketService(
        TicketRepository ticketRepository,
        UserRepository userRepository
        ) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        }

    public List<AgentTicketResponse> getActiveTickets() {
        return ticketRepository.findByStatusIn(ACTIVE_STATUSES)
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        (Ticket ticket) ->
                                                PRIORITY_ORDER.getOrDefault(ticket.getPriority(), Integer.MAX_VALUE)
                                )
                                .thenComparing(Ticket::getCreatedAt)
                )
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AgentTicketResponse assignTicket( 
        Long ticketId,
        Long agentId
    ) {
        Ticket ticket = getTicketOrThrow(ticketId);

        if (
                ticket.getStatus() == TicketStatus.RESOLVED ||
                ticket.getStatus() == TicketStatus.CLOSED
        ) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Un tichet finalizat nu mai poate fi atribuit"
                );
        }

        User agent = userRepository.findById(agentId)
        .orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agentul nu a fost găsit"
                )
        );

        if (
                agent.getRole() != Role.AGENT &&
                agent.getRole() != Role.ADMIN
        ) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Utilizatorul selectat nu este agent"
        );
        }

        ticket.assignTo(agent);
        Ticket savedTicket = ticketRepository.save(ticket);

        return toResponse(savedTicket);
    }

    @Transactional
    public AgentTicketResponse changeStatus(
        Long ticketId,
        TicketStatus newStatus
    ) {
        Ticket ticket = getTicketOrThrow(ticketId);

        if (newStatus == TicketStatus.ESCALATED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Pentru escaladare trebuie completat si motivul"
                );
        }

        ticket.changeStatus(newStatus);
        Ticket savedTicket = ticketRepository.save(ticket);

        return toResponse(savedTicket);
    }

    @Transactional
    public AgentTicketResponse escalateTicket (
        Long ticketId,
        String reason
    ) {
        Ticket ticket = getTicketOrThrow(ticketId);

        if (
                ticket.getStatus() == TicketStatus.RESOLVED ||
                ticket.getStatus() == TicketStatus.CLOSED
        ) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Un tichet finalizat nu poate fi escaladat"
                );
        }

        ticket.escalate(reason.trim());
        Ticket savedTicket = ticketRepository.save(ticket);

        return toResponse(savedTicket);
    }

    private AgentTicketResponse toResponse(Ticket ticket) {
        return new AgentTicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getTitle(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getAssignedAgentId(),
                ticket.getEscalationReason(),
                ticket.isSlaBreached(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

    private Ticket getTicketOrThrow(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                       new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tichetul nu a fost gasit"
                       ) 
        );
    }
}