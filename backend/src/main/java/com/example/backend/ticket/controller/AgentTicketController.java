package com.example.backend.ticket.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.ticket.dto.AgentTicketDetailsResponse;
import com.example.backend.ticket.dto.AgentTicketResponse;
import com.example.backend.ticket.dto.AssignTicketRequest;
import com.example.backend.ticket.dto.ChangeTicketStatusRequest;
import com.example.backend.ticket.dto.EscalateTicketRequest;
import com.example.backend.ticket.service.AgentTicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agent/tickets")
@CrossOrigin(origins = "http://localhost:3000")
public class AgentTicketController {
    private final AgentTicketService agentTicketService;

    public AgentTicketController(AgentTicketService agentTicketService) {
        this.agentTicketService = agentTicketService;
    }

    @GetMapping
    public List<AgentTicketResponse> getActiveTickets() {
        return agentTicketService.getActiveTickets();
    }

    @GetMapping("/{ticketId}")
    public AgentTicketDetailsResponse getTicketDetails(
        @PathVariable Long ticketId
    ) {
        return agentTicketService.getTicketDetails(ticketId);
    }

    @PutMapping("/{ticketId}/assign")
    public AgentTicketResponse assignTicket(
        @PathVariable Long ticketId,
        @Valid @RequestBody AssignTicketRequest request, Authentication authentication
    ) {
        return agentTicketService.assignTicket(
            ticketId,
            request.agentId(),
            authentication
        );
    }

    @PutMapping("/{ticketId}/status")
    public AgentTicketResponse changeStatus(
        @PathVariable Long ticketId,
        @Valid @RequestBody ChangeTicketStatusRequest request, Authentication authentication
    ) {
        return agentTicketService.changeStatus(
            ticketId, 
            request.status(),
            authentication
        );
    }

    @PutMapping("/{ticketId}/escalate")
    public AgentTicketResponse escalateTicket(
        @PathVariable Long ticketId,
        @Valid @RequestBody EscalateTicketRequest request, Authentication authentication
    ) {
        return agentTicketService.escalateTicket(
            ticketId, 
            request.reason(),
            authentication
        );
    }
}
