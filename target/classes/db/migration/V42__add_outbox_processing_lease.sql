ALTER TABLE dt_outbox_events_oe
    ADD COLUMN claimed_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN claimed_by VARCHAR(100);

CREATE INDEX idx_oe_processing_lease
    ON dt_outbox_events_oe (
        status,
        claimed_at
    );