package com.example.backend.sla.controller;

import com.example.backend.sla.dto.SlaConfigResponse;
import com.example.backend.sla.dto.UpdateSlaConfigRequest;
import com.example.backend.sla.service.SlaConfigService;
import com.example.backend.common.model.TicketPriority;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/admin/sla")
@CrossOrigin(origins = "http://localhost:3000")
public class SlaController {
    private final SlaConfigService slaConfigService;
    public SlaController(SlaConfigService slaConfigService) {
        this.slaConfigService = slaConfigService;
    }

    @GetMapping
    public List<SlaConfigResponse> findAll() {
        return slaConfigService.findAll();
    }

    @PutMapping("/{priority}")
    public SlaConfigResponse update(@PathVariable String priority, @Valid @RequestBody UpdateSlaConfigRequest request) {
        return slaConfigService.update(parsePriority(priority), request);   
    }

    private TicketPriority parsePriority(String value) {
        try {
            return TicketPriority.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioritatea SLA invalida: " + value);
        }
    }
}
