package com.example.backend.messages.repository;

import com.example.backend.messages.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    /*
     * Folosit de angajat.
     * Returnează doar mesajele publice.
     */
    List<Message>
    findAllByTicketIdAndInternalFalseOrderByCreatedAtAsc(
            Long ticketId
    );

    /*
     * Va putea fi folosit de agent.
     * Returnează mesajele publice și interne.
     */
    List<Message>
    findAllByTicketIdOrderByCreatedAtAsc(
            Long ticketId
    );
}