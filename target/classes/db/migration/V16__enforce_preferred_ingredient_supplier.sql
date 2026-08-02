-- ============================================================
-- Only one active preferred supplier per ingredient
-- ============================================================

CREATE UNIQUE INDEX uk_ing_supplier_one_preferred
ON dt_ingredient_suppliers_is(ingredient_id)
WHERE is_preferred = TRUE
  AND is_active = TRUE;