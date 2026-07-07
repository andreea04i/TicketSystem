CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,

    ticket_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,

    content TEXT NOT NULL,

    is_internal BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_messages_ticket
        FOREIGN KEY (ticket_id)
        REFERENCES tickets(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id)
        REFERENCES users(id)
);

CREATE INDEX idx_messages_ticket_created_at
    ON messages(ticket_id, created_at);

CREATE INDEX idx_messages_author
    ON messages(author_id);