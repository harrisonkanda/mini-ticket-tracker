-- Support tickets. Enums are stored as text and guarded by CHECK constraints,
-- which keeps adding a new value a one-line migration (no ALTER TYPE dance).
CREATE TABLE tickets (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(200)  NOT NULL,
    description     TEXT,
    status          VARCHAR(32)   NOT NULL,
    priority        VARCHAR(32)   NOT NULL,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_tickets_status   CHECK (status IN ('OPEN', 'IN_PROGRESS', 'CLOSED')),
    CONSTRAINT chk_tickets_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))
);

-- Serves the main list query: filter by status, newest first.
CREATE INDEX idx_tickets_status_created_at
    ON tickets (status, created_at DESC);

-- Serves the unfiltered list (newest first across all statuses).
CREATE INDEX idx_tickets_created_at
    ON tickets (created_at DESC);
