-- ============================================================
-- HIGH-VOLUME QUERY OPTIMIZATION
-- ============================================================


-- ============================================================
-- SALES HISTORY
-- ============================================================

CREATE INDEX idx_sal_status_sold_at_id
    ON dt_sales_sal (
        status_id,
        sold_at DESC,
        id DESC
    );


-- ============================================================
-- INVENTORY MOVEMENT HISTORY
-- ============================================================

DROP INDEX IF EXISTS idx_im_ingredient_occurred;

CREATE INDEX idx_im_ingredient_occurred
    ON dt_inventory_movements_im (
        ingredient_id,
        occurred_at DESC,
        id DESC
    );


-- ============================================================
-- PURCHASE ORDER SUPPLIER HISTORY
-- ============================================================

DROP INDEX IF EXISTS idx_po_supplier;

CREATE INDEX idx_po_supplier
    ON dt_purchase_orders_po (
        supplier_id,
        order_date DESC,
        id DESC
    );


-- ============================================================
-- AUDIT ENTITY HISTORY
-- ============================================================

DROP INDEX IF EXISTS idx_al_entity;

CREATE INDEX idx_al_entity
    ON dt_audit_logs_al (
        entity_type,
        entity_id,
        created_at DESC,
        id DESC
    );