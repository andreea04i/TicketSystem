package com.example.backend.ticket.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.common.model.TicketCategory;
import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;
import com.example.backend.rabbitmq.TicketEventPublisher;
import com.example.backend.security.CurrentUserService;
import com.example.backend.ticket.dto.AgentTicketDetailsResponse;
import com.example.backend.ticket.dto.AgentTicketResponse;
import com.example.backend.ticket.history.TicketHistory;
import com.example.backend.ticket.history.TicketHistoryRepository;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.ticket.repository.TicketRepository;
import com.example.backend.user.model.Role;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AgentTicketService {

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
    private final TicketHistoryRepository ticketHistoryRepository;

    public AgentTicketService(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService,
            TicketEventPublisher ticketEventPublisher,
            TicketHistoryRepository ticketHistoryRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.ticketEventPublisher = ticketEventPublisher;
        this.ticketHistoryRepository = ticketHistoryRepository;
    }

    public List<AgentTicketResponse> getTickets(
            TicketStatus status,
            TicketPriority priority,
            TicketCategory category,
            Long assignedAgentId
    ) {
        return ticketRepository.findAll()
                .stream()
                .filter(ticket -> status == null || ticket.getStatus() == status)
                .filter(ticket -> priority == null || ticket.getPriority() == priority)
                .filter(ticket -> category == null || ticket.getCategory() == category)
                .filter(ticket -> assignedAgentId == null
                        || assignedAgentId.equals(ticket.getAssignedAgentId()))
                .sorted(
                        Comparator
                                .comparingInt(
                                        (Ticket ticket) ->
                                                PRIORITY_ORDER.getOrDefault(
                                                        ticket.getPriority(),
                                                        Integer.MAX_VALUE
                                                )
                                )
                                .thenComparing(
                                        Ticket::getCreatedAt,
                                        Comparator.reverseOrder()
                                )
                )
                .map(this::toResponse)
                .toList();
    }

    public AgentTicketDetailsResponse getTicketDetails(Long ticketId) {
        Ticket ticket = getTicketOrThrow(ticketId);

        return toDetailsResponse(ticket);
    }

    @Transactional
    public AgentTicketResponse claimTicket(
            Long ticketId,
            Authentication authentication
    ) {
        User actor = currentUserService.getCurrentUser(authentication);
        Ticket ticket = getTicketOrThrow(ticketId);

        ensureAgentOrAdmin(actor);
        ensureTicketCanBeWorked(ticket);

        if (ticket.getAssignedAgent() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Tichetul este deja atribuit unui agent"
            );
        }

        TicketStatus previousStatus = ticket.getStatus();

        ticket.assignTo(actor);
        Ticket savedTicket = ticketRepository.save(ticket);

        saveHistoryIfStatusChanged(
                savedTicket,
                actor,
                previousStatus,
                savedTicket.getStatus(),
                "Tichet preluat de agent"
        );

        ticketEventPublisher.publishTicketAssigned(
                savedTicket,
                actor.getId(),
                actor.getId()
        );

        return toResponse(savedTicket);
    }

    @Transactional
    public AgentTicketResponse assignTicket(
            Long ticketId,
            Long agentId,
            Authentication authentication
    ) {
        User actor = currentUserService.getCurrentUser(authentication);

        if (actor.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Doar adminul poate atribui tichete altui agent"
            );
        }

        Ticket ticket = getTicketOrThrow(ticketId);

        ensureTicketCanBeWorked(ticket);

        User agent = userRepository.findById(agentId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Agentul nu a fost găsit"
                        )
                );

        ensureAgentOrAdmin(agent);

        TicketStatus previousStatus = ticket.getStatus();

        ticket.assignTo(agent);
        Ticket savedTicket = ticketRepository.save(ticket);

        saveHistoryIfStatusChanged(
                savedTicket,
                actor,
                previousStatus,
                savedTicket.getStatus(),
                "Tichet atribuit agentului " + agent.getFullName()
        );

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
                    "Pentru escaladare trebuie completat motivul"
            );
        }

        TicketStatus previousStatus = ticket.getStatus();

        if (previousStatus == newStatus) {
            return toResponse(ticket);
        }

        validateStatusTransition(previousStatus, newStatus);

        ticket.changeStatus(newStatus);
        Ticket savedTicket = ticketRepository.save(ticket);

        saveHistoryIfStatusChanged(
                savedTicket,
                actor,
                previousStatus,
                newStatus,
                "Status schimbat manual"
        );

        ticketEventPublisher.publishStatusChanged(
                savedTicket,
                actor.getId(),
                savedTicket.getCreatedBy().getId(),
                previousStatus
        );

        return toResponse(savedTicket);
    }

    @Transactional
    public AgentTicketResponse escalateTicket(
            Long ticketId,
            String reason,
            Authentication authentication
    ) {
        User actor = currentUserService.getCurrentUser(authentication);
        Ticket ticket = getTicketOrThrow(ticketId);

        TicketStatus previousStatus = ticket.getStatus();

        validateStatusTransition(
                previousStatus,
                TicketStatus.ESCALATED
        );

        ticket.escalate(reason.trim());
        Ticket savedTicket = ticketRepository.save(ticket);

        saveHistoryIfStatusChanged(
                savedTicket,
                actor,
                previousStatus,
                TicketStatus.ESCALATED,
                reason
        );

        ticketEventPublisher.publishTicketEscalated(
                savedTicket,
                actor.getId(),
                savedTicket.getCreatedBy().getId()
        );

        return toResponse(savedTicket);
    }

    private void validateStatusTransition(
            TicketStatus oldStatus,
            TicketStatus newStatus
    ) {
        boolean allowed = switch (oldStatus) {
            case OPEN -> newStatus == TicketStatus.IN_PROGRESS;
            case IN_PROGRESS -> newStatus == TicketStatus.RESOLVED
                    || newStatus == TicketStatus.ESCALATED;
            case ESCALATED -> newStatus == TicketStatus.IN_PROGRESS;
            case RESOLVED -> newStatus == TicketStatus.CLOSED;
            case CLOSED -> false;
        };

        if (!allowed) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Tranziție de status invalidă: "
                            + oldStatus + " -> " + newStatus
            );
        }
    }

    private void saveHistoryIfStatusChanged(
            Ticket ticket,
            User actor,
            TicketStatus oldStatus,
            TicketStatus newStatus,
            String reason
    ) {
        if (oldStatus == newStatus) {
            return;
        }

        ticketHistoryRepository.save(
                TicketHistory.create(
                        ticket,
                        actor,
                        oldStatus,
                        newStatus,
                        reason
                )
        );
    }

    private void ensureTicketCanBeWorked(Ticket ticket) {
        if (ticket.getStatus() == TicketStatus.RESOLVED
                || ticket.getStatus() == TicketStatus.CLOSED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Un tichet finalizat nu mai poate fi atribuit sau preluat"
            );
        }
    }

    private void ensureAgentOrAdmin(User user) {
        if (user.getRole() != Role.AGENT
                && user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Utilizatorul selectat nu este agent"
            );
        }
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
                                "Tichetul nu a fost găsit"
                        )
                );
    }
}