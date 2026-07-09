package com.example.backend.admin.report.service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.admin.report.dto.AverageResolutionTimeResponse;
import com.example.backend.admin.report.dto.SlaBreachResponse;
import com.example.backend.admin.report.dto.TicketsByCategoryResponse;
import com.example.backend.common.model.TicketCategory;
import com.example.backend.common.model.TicketPriority;
import com.example.backend.ticket.model.Ticket;
import com.example.backend.ticket.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReportService {

    private final TicketRepository ticketRepository;

    public List<TicketsByCategoryResponse> getTicketsByCategory() {
        Map<TicketCategory, Long> countsByCategory =
                ticketRepository.findAll()
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getCategory,
                                        Collectors.counting()
                                )
                        );

        return Arrays.stream(TicketCategory.values())
                .map(category -> new TicketsByCategoryResponse(
                        category,
                        countsByCategory.getOrDefault(category, 0L)
                ))
                .toList();
    }

    public AverageResolutionTimeResponse getAverageResolutionTime() {
        List<Ticket> resolvedTickets =
                ticketRepository.findAll()
                        .stream()
                        .filter(ticket -> ticket.getResolvedAt() != null)
                        .toList();

        if (resolvedTickets.isEmpty()) {
            return new AverageResolutionTimeResponse(0, 0.0);
        }

        double averageHours =
                resolvedTickets.stream()
                        .mapToLong(ticket ->
                                Duration.between(
                                        ticket.getCreatedAt(),
                                        ticket.getResolvedAt()
                                ).toMinutes()
                        )
                        .average()
                        .orElse(0.0) / 60.0;

        return new AverageResolutionTimeResponse(
                resolvedTickets.size(),
                roundTwoDecimals(averageHours)
        );
    }

    public List<SlaBreachResponse> getSlaBreaches() {
        List<Ticket> tickets = ticketRepository.findAll();

        return Arrays.stream(TicketPriority.values())
                .map(priority -> buildSlaBreachResponse(priority, tickets))
                .toList();
    }

    private SlaBreachResponse buildSlaBreachResponse(
            TicketPriority priority,
            List<Ticket> tickets
    ) {
        long totalTickets =
                tickets.stream()
                        .filter(ticket -> ticket.getPriority() == priority)
                        .count();

        long breachedTickets =
                tickets.stream()
                        .filter(ticket -> ticket.getPriority() == priority)
                        .filter(Ticket::isSlaBreached)
                        .count();

        double breachRatePercent =
                totalTickets == 0
                        ? 0.0
                        : breachedTickets * 100.0 / totalTickets;

        return new SlaBreachResponse(
                priority,
                totalTickets,
                breachedTickets,
                roundTwoDecimals(breachRatePercent)
        );
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}