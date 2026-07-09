package com.example.backend.ticket.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.common.model.TicketStatus;
import com.example.backend.ticket.model.Ticket;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatusIn(
            Collection<TicketStatus> statuses
    );

    List<Ticket>
    findAllByCreatedByIdOrderByCreatedAtDesc(
            Long userId
    );

    Optional<Ticket> findByIdAndCreatedById(
            Long ticketId,
            Long userId
    );

    List<Ticket> findAllByStatusInAndSlaBreachedFalse(
        Collection<TicketStatus> statuses
);
}