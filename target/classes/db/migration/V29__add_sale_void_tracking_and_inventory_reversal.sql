-- ============================================================
-- SALE VOID TRACKING
-- ============================================================

ALTER TABLE dt_sales_sal
ADD COLUMN voided_at TIMESTAMPTZ,
ADD COLUMN voided_by_user_id BIGINT,
ADD COLUMN void_reason VARCHAR(500);

ALTER TABLE dt_sales_sal
ADD CONSTRAINT fk_sal_voided_by
FOREIGN KEY (voided_by_user_id)
REFERENCES dt_users_usr(id);


CREATE INDEX idx_sal_voided_at
    ON dt_sales_sal(voided_at);

CREATE INDEX idx_sal_voided_by
    ON dt_sales_sal(voided_by_user_id);


-- ============================================================
-- INVENTORY REVERSAL MOVEMENT
-- ============================================================

INSERT INTO lt_inventory_movement_types_imt (
    code,
    name,
    description,
    direction,
    is_manual_allowed,
    display_order,
    is_active
)
VALUES (
    'SALE_REVERSAL',
    'Sale Reversal',
    'Restores inventory previously consumed by a voided sale.',
    1,
    FALSE,
    75,
    TRUE
)
ON CONFLICT (code)
DO NOTHING;