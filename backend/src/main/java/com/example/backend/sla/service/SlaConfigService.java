package com.example.backend.sla.service;

import com.example.backend.common.model.TicketPriority;
import com.example.backend.sla.dto.SlaConfigResponse;
import com.example.backend.sla.dto.UpdateSlaConfigRequest;
import com.example.backend.sla.model.SlaConfig;
import com.example.backend.sla.repository.SlaConfigRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)

public class SlaConfigService {
    private final SlaConfigRepository slaConfigRepository;

    public SlaConfigService(SlaConfigRepository slaConfigRepository) {
        this.slaConfigRepository = slaConfigRepository;
    }

    public List<SlaConfigResponse> findAll() {
        return slaConfigRepository.findAll().stream()
                .sorted(Comparator.comparingInt(config -> config.getPriority().ordinal()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SlaConfigResponse update(TicketPriority priority, UpdateSlaConfigRequest request) {
        SlaConfig config = slaConfigRepository.findByPriority(priority)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nu exista o configuratie SLA pentru " + priority));
        config.setResolutionHours(request.resolutionHours());
        SlaConfig savedConfig = slaConfigRepository.save(config);
        return toResponse(savedConfig);
    }

    private SlaConfigResponse toResponse(SlaConfig config) {
        return new SlaConfigResponse(
                config.getId(),
                config.getPriority(),
                config.getResolutionHours(),
                config.getUpdatedAt()
        );
    }
}
