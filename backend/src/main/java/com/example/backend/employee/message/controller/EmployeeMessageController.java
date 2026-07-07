package com.example.backend.employee.message.controller;

import com.example.backend.employee.message.dto.AddPublicMessageRequest;
import com.example.backend.employee.message.service.EmployeeMessageService;
import com.example.backend.messages.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/employee/tickets/{ticketId}/messages"
)
@RequiredArgsConstructor
public class EmployeeMessageController {

    private final EmployeeMessageService
            employeeMessageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse addPublicMessage(
            @PathVariable Long ticketId,

            @Valid
            @RequestBody
            AddPublicMessageRequest request,

            Authentication authentication
    ) {
        return employeeMessageService.addPublicMessage(
                ticketId,
                request,
                authentication
        );
    }

    @GetMapping
    public List<MessageResponse> findPublicMessages(
            @PathVariable Long ticketId,
            Authentication authentication
    ) {
        return employeeMessageService.findPublicMessages(
                ticketId,
                authentication
        );
    }
}