package com.example.backend.sla.repository;

import com.example.backend.common.model.TicketPriority;
import com.example.backend.sla.model.SlaConfig;
import org.springframework.data.jpa.repository.JpaRepository;   

import java.util.Optional;

public interface SlaConfigRepository extends JpaRepository<SlaConfig, Long> {
    Optional<SlaConfig> findByPriority(TicketPriority priority);
}
