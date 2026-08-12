CREATE TABLE dt_outbox_events_oe (
    id BIGSERIAL PRIMARY KEY,

    event_id UUID NOT NULL,

    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id BIGINT,

    event_type VARCHAR(100) NOT NULL,

    payload TEXT NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    attempts INTEGER NOT NULL DEFAULT 0,

    next_attempt_at TIMESTAMP WITH TIME ZONE,

    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,

    published_at TIMESTAMP WITH TIME ZONE,

    last_error TEXT,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_outbox_event_id
        UNIQUE (event_id),

    CONSTRAINT ck_outbox_status
        CHECK (
            status IN (
                'PENDING',
                'PROCESSING',
                'PUBLISHED',
                'FAILED'
            )
        ),

    CONSTRAINT ck_outbox_attempts
        CHECK (attempts >= 0)
);

CREATE INDEX idx_oe_pending
    ON dt_outbox_events_oe (
        status,
        next_attempt_at,
        id
    );
    
CREATE INDEX idx_oe_aggregate
    ON dt_outbox_events_oe (
        aggregate_type,
        aggregate_id,
        occurred_at DESC
    );
    
