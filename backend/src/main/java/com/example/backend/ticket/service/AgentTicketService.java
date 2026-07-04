package com.example.backend.ticket.service;

import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;
import com.example.backend.ticket.dto.AgentTicketResponse;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
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

    public AgentTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<AgentTicketResponse> getActiveTickets() {
        return ticketRepository.findByStatusIn(ACTIVE_STATUSES)
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        (Ticket ticket) ->
                                                PRIORITY_ORDER.get(ticket.getPriority())
                                )
                                .thenComparing(Ticket::getCreatedAt)
                )
                .map(this::toResponse)
                .toList();
    }

    private AgentTicketResponse toResponse(Ticket ticket) {
        return new AgentTicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getTitle(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.isSlaBreached(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}