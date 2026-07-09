package com.example.backend.sla.job;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.common.model.TicketPriority;
import com.example.backend.common.model.TicketStatus;
import com.example.backend.sla.model.SlaConfig;
import com.example.backend.sla.repository.SlaConfigRepository;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.ticket.repository.TicketRepository;

@Component
public class SlaMonitoringJob {

    private static final List<TicketStatus> ACTIVE_STATUSES =
            List.of(
                    TicketStatus.OPEN,
                    TicketStatus.IN_PROGRESS,
                    TicketStatus.ESCALATED
            );

    private final TicketRepository ticketRepository;

    private final SlaConfigRepository slaConfigRepository;

    public SlaMonitoringJob(
            TicketRepository ticketRepository,
            SlaConfigRepository slaConfigRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.slaConfigRepository = slaConfigRepository;
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void markOverdueTickets() {
        Instant now = Instant.now();

        Map<TicketPriority, Integer> resolutionHoursByPriority =
                slaConfigRepository.findAll()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        SlaConfig::getPriority,
                                        SlaConfig::getResolutionHours
                                )
                        );

        List<Ticket> activeTickets =
                ticketRepository
                        .findAllByStatusInAndSlaBreachedFalse(
                                ACTIVE_STATUSES
                        );

        for (Ticket ticket : activeTickets) {
            Integer resolutionHours =
                    resolutionHoursByPriority.get(
                            ticket.getPriority()
                    );

            if (resolutionHours == null) {
                continue;
            }

            Instant deadline =
                    ticket.getCreatedAt()
                            .plusSeconds(
                                    resolutionHours * 60L * 60L
                            );

            if (deadline.isBefore(now)) {
                ticket.markSlaBreached();
            }
        }
    }
}