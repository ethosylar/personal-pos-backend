-- ============================================================
-- Prevent duplicate system-generated inventory movements
--
-- Future examples:
--
-- SALE_ITEM + saleItemId + ingredient + SALE_USAGE
-- GOODS_RECEIPT_ITEM + receiptItemId + ingredient + PURCHASE_RECEIPT
--
-- Manual movements normally have NULL reference_id and are
-- therefore unaffected by this constraint.
-- ============================================================

CREATE UNIQUE INDEX uk_im_reference_ingredient_type
ON dt_inventory_movements_im (
    reference_type,
    reference_id,
    ingredient_id,
    movement_type_id
)
WHERE reference_type IS NOT NULL
  AND reference_id IS NOT NULL;