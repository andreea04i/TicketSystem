package com.example.backend.agent.message.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.agent.message.dto.AddAgentMessageRequest;
import com.example.backend.agent.message.service.AgentMessageService;
import com.example.backend.messages.dto.MessageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agent/tickets/{ticketId}/messages")
public class AgentMessageController {

    private final AgentMessageService agentMessageService;

    @GetMapping
    public List<MessageResponse> findAllMessages(
            @PathVariable Long ticketId
    ) {
        return agentMessageService.findAllMessagesForAgent(
                ticketId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse addMessage(
            @PathVariable Long ticketId,
            @Valid @RequestBody AddAgentMessageRequest request,
            Authentication authentication
    ) {
        return agentMessageService.addMessageAsAgent(
                ticketId,
                request,
                authentication
        );
    }
}