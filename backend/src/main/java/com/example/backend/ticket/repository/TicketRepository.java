package com.example.backend.ticket.repository;

import com.example.backend.common.model.TicketStatus;
import com.example.backend.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    // Folosit de dashboardul agentului creat de Daria.
    List<Ticket> findByStatusIn(
            Collection<TicketStatus> statuses
    );

    // Tichetele utilizatorului autentificat.
    List<Ticket>
    findAllByCreatedByIdOrderByCreatedAtDesc(
            Long userId
    );

    // Caută tichetul numai dacă aparține angajatului.
    Optional<Ticket> findByIdAndCreatedById(
            Long ticketId,
            Long userId
    );
}