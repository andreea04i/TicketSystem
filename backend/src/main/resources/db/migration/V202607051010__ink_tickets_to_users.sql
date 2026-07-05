-- Utilizator tehnic folosit numai pentru tichetele demo existente.
-- Parola corespunzătoare hash-ului nu este cunoscută și contul
-- nu este destinat autentificării.

INSERT INTO users (
    full_name,
    email,
    password_hash,
    role
)
VALUES (
    'System Sample Owner',
    'system@ticketsystem.local',
    '$2y$10$5WoU7iEnvRhWZkEWcMtAQuhiQjmcfB2WGIXrK7fYqAdfv77N3Q0fi',
    'EMPLOYEE'
)
ON CONFLICT (email) DO NOTHING;

ALTER TABLE tickets
    ADD COLUMN created_by_id BIGINT,
    ADD COLUMN assigned_agent_id BIGINT,
    ADD COLUMN resolved_at TIMESTAMPTZ,
    ADD COLUMN closed_at TIMESTAMPTZ;

-- Cele șase tichete demo existente primesc un proprietar tehnic.
UPDATE tickets
SET created_by_id = (
    SELECT id
    FROM users
    WHERE email = 'system@ticketsystem.local'
)
WHERE created_by_id IS NULL;

ALTER TABLE tickets
    ALTER COLUMN created_by_id SET NOT NULL;

ALTER TABLE tickets
    ADD CONSTRAINT fk_tickets_created_by
        FOREIGN KEY (created_by_id)
        REFERENCES users(id),

    ADD CONSTRAINT fk_tickets_assigned_agent
        FOREIGN KEY (assigned_agent_id)
        REFERENCES users(id);

CREATE INDEX idx_tickets_created_by
    ON tickets(created_by_id);

CREATE INDEX idx_tickets_assigned_agent
    ON tickets(assigned_agent_id);