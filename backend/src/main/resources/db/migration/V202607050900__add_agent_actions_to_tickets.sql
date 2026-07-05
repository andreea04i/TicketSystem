ALTER TABLE tickets
    ADD COLUMN assigned_agent_id BIGINT,
    ADD COLUMN escalation_reason TEXT;