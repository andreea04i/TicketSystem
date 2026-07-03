CREATE TABLE sla_config (
    id BIGSERIAL PRIMARY KEY,
    priority VARCHAR(20) NOT NULL UNIQUE,
    resolution_hours INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_sla_resolution_hours CHECK (resolution_hours > 0),

    CONSTRAINT chk_sla_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

INSERT INTO sla_config (priority, resolution_hours) VALUES
('LOW', 72),
('MEDIUM', 24),
('HIGH', 8),
('CRITICAL', 2);