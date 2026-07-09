package com.example.backend.ticket.history;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketHistoryRepository
        extends JpaRepository<TicketHistory, Long> {

    List<TicketHistory> findAllByTicketIdOrderByChangedAtAsc(Long ticketId);
}