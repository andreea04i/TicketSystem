package com.example.backend.ticket.controller;

import com.example.backend.common.model.TicketCategory;
import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;
import com.example.backend.ticket.dto.AgentTicketDetailsResponse;
import com.example.backend.ticket.dto.AgentTicketResponse;
import com.example.backend.ticket.dto.AssignTicketRequest;
import com.example.backend.ticket.dto.ChangeTicketStatusRequest;
import com.example.backend.ticket.dto.EscalateTicketRequest;
import com.example.backend.ticket.service.AgentTicketService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agent/tickets")
@CrossOrigin(origins = "http://localhost:3000")
public class AgentTicketController {

    private final AgentTicketService agentTicketService;

    public AgentTicketController(AgentTicketService agentTicketService) {
        this.agentTicketService = agentTicketService;
    }

    @GetMapping
    public List<AgentTicketResponse> getTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) TicketCategory category,
            @RequestParam(required = false) Long assignedAgentId
    ) {
        return agentTicketService.getTickets(
                status,
                priority,
                category,
                assignedAgentId
        );
    }

    @GetMapping("/{ticketId}")
    public AgentTicketDetailsResponse getTicketDetails(
            @PathVariable Long ticketId
    ) {
        return agentTicketService.getTicketDetails(ticketId);
    }

    @PatchMapping("/{ticketId}/claim")
    public AgentTicketResponse claimTicket(
            @PathVariable Long ticketId,
            Authentication authentication
    ) {
        return agentTicketService.claimTicket(
                ticketId,
                authentication
        );
    }

    @PatchMapping("/{ticketId}/assign")
    public AgentTicketResponse assignTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody AssignTicketRequest request,
            Authentication authentication
    ) {
        return agentTicketService.assignTicket(
                ticketId,
                request.agentId(),
                authentication
        );
    }

    @PatchMapping("/{ticketId}/status")
    public AgentTicketResponse changeStatus(
            @PathVariable Long ticketId,
            @Valid @RequestBody ChangeTicketStatusRequest request,
            Authentication authentication
    ) {
        return agentTicketService.changeStatus(
                ticketId,
                request.status(),
                authentication
        );
    }

    @PostMapping("/{ticketId}/escalate")
    public AgentTicketResponse escalateTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody EscalateTicketRequest request,
            Authentication authentication
    ) {
        return agentTicketService.escalateTicket(
                ticketId,
                request.reason(),
                authentication
        );
    }
}