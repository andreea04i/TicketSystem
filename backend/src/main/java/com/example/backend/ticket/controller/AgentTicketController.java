package com.example.backend.ticket.controller;

import com.example.backend.ticket.dto.AgentTicketResponse;
import com.example.backend.ticket.service.AgentTicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

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
    public List<AgentTicketResponse> getActiveTickets() {
        return agentTicketService.getActiveTickets();
    }
}
