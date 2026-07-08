package com.example.backend.ticket.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;
import com.example.backend.rabbitmq.TicketEventPublisher;
import com.example.backend.security.CurrentUserService;
import com.example.backend.ticket.dto.AgentTicketDetailsResponse;
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
    private final CurrentUserService currentUserService;
    private final TicketEventPublisher ticketEventPublisher;

    public AgentTicketService(
        TicketRepository ticketRepository,
        UserRepository userRepository,
        CurrentUserService currentUserService,
        TicketEventPublisher ticketEventPublisher
        ) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.ticketEventPublisher = ticketEventPublisher;
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

    public AgentTicketDetailsResponse getTicketDetails(
        Long ticketId
    ) {
        Ticket ticket = getTicketOrThrow(ticketId);

        return toDetailsResponse(ticket);
    }

    @Transactional
    public AgentTicketResponse assignTicket( 
        Long ticketId,
        Long agentId,
        Authentication authentication
    ) {
        User actor = currentUserService.getCurrentUser(authentication);
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

        ticketEventPublisher.publishTicketAssigned(
                savedTicket,
                actor.getId(),
                agent.getId()
        );

        return toResponse(savedTicket);
    }

    @Transactional
    public AgentTicketResponse changeStatus(
        Long ticketId,
        TicketStatus newStatus,
        Authentication authentication
    ) {
        User actor = currentUserService.getCurrentUser(authentication);

        Ticket ticket = getTicketOrThrow(ticketId);

        if (newStatus == TicketStatus.ESCALATED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Pentru escaladare trebuie completat si motivul"
                );
        }
        TicketStatus previousStatus = ticket.getStatus();

        ticket.changeStatus(newStatus);
        Ticket savedTicket = ticketRepository.save(ticket);

        if (previousStatus != newStatus){
                ticketEventPublisher.publishStatusChanged(
                        savedTicket,
                        actor.getId(),
                        savedTicket.getCreatedBy().getId(),
                        previousStatus
                );
        }

        return toResponse(savedTicket);
    }

    @Transactional
    public AgentTicketResponse escalateTicket (
        Long ticketId,
        String reason,
        Authentication authentication
    ) {
        User actor = currentUserService.getCurrentUser(authentication);

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

        ticketEventPublisher.publishTicketEscalated(
                savedTicket,
                actor.getId(),
                savedTicket.getCreatedBy().getId()
        );
        
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

    private AgentTicketDetailsResponse toDetailsResponse(
        Ticket ticket
    ) {
        User createdBy = ticket.getCreatedBy();
        User assignedAgent = ticket.getAssignedAgent();

        return new AgentTicketDetailsResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getTitle(),
                ticket.getDescription(),

                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),

                createdBy.getId(),
                createdBy.getFullName(),
                createdBy.getEmail(),

                assignedAgent == null ? null : assignedAgent.getId(),

                assignedAgent == null ? null : assignedAgent.getFullName(),

                assignedAgent == null ? null : assignedAgent.getEmail(),

                ticket.getEscalationReason(),
                ticket.isSlaBreached(),

                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getResolvedAt(),
                ticket.getClosedAt()
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