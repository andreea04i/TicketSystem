package com.example.backend.ticket.repository;

import com.example.backend.common.model.TicketStatus;
import com.example.backend.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatusIn(Collection<TicketStatus> statuses);
}
