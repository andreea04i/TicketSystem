CREATE TABLE ticket_history (
    id BIGSERIAL PRIMARY KEY,

    ticket_id BIGINT NOT NULL,
    changed_by_user_id BIGINT NOT NULL,

    old_status VARCHAR(30) NOT NULL,
    new_status VARCHAR(30) NOT NULL,

    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    reason TEXT,

    CONSTRAINT fk_ticket_history_ticket
        FOREIGN KEY (ticket_id)
        REFERENCES tickets(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ticket_history_changed_by
        FOREIGN KEY (changed_by_user_id)
        REFERENCES users(id)
);

CREATE INDEX idx_ticket_history_ticket_changed_at
    ON ticket_history(ticket_id, changed_at);

CREATE INDEX idx_ticket_history_changed_by
    ON ticket_history(changed_by_user_id);