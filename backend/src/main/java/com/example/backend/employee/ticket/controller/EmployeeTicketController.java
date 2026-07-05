package com.example.backend.employee.ticket.controller;

import com.example.backend.employee.ticket.dto.CreateTicketRequest;
import com.example.backend.employee.ticket.dto.EmployeeTicketResponse;
import com.example.backend.employee.ticket.service.EmployeeTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/tickets")
@RequiredArgsConstructor
public class EmployeeTicketController {

    private final EmployeeTicketService employeeTicketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeTicketResponse create(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication authentication
    ) {
        return employeeTicketService.create(
                request,
                authentication
        );
    }

    @GetMapping
    public List<EmployeeTicketResponse> findOwnTickets(
            Authentication authentication
    ) {
        return employeeTicketService.findOwnTickets(
                authentication
        );
    }

    @GetMapping("/{ticketId}")
    public EmployeeTicketResponse findOwnTicket(
            @PathVariable Long ticketId,
            Authentication authentication
    ) {
        return employeeTicketService.findOwnTicket(
                ticketId,
                authentication
        );
    }

    @PatchMapping("/{ticketId}/close")
    public EmployeeTicketResponse closeOwnTicket(
            @PathVariable Long ticketId,
            Authentication authentication
    ) {
        return employeeTicketService.closeOwnTicket(
                ticketId,
                authentication
        );
    }
}