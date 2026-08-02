-- ============================================================
-- INVENTORY MOVEMENT TYPES
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
VALUES

    (
        'OPENING_STOCK',
        'Opening Stock',
        'Initial stock balance when inventory tracking begins.',
        1,
        TRUE,
        10,
        TRUE
    ),

    (
        'PURCHASE_RECEIPT',
        'Purchase Receipt',
        'Stock received through purchasing or goods receipt.',
        1,
        FALSE,
        20,
        TRUE
    ),

    (
        'ADJUSTMENT_IN',
        'Adjustment In',
        'Manual stock increase due to stock adjustment.',
        1,
        TRUE,
        30,
        TRUE
    ),

    (
        'ADJUSTMENT_OUT',
        'Adjustment Out',
        'Manual stock decrease due to stock adjustment.',
        -1,
        TRUE,
        40,
        TRUE
    ),

    (
        'WASTE',
        'Waste',
        'Stock removed because of waste, spoilage or damage.',
        -1,
        TRUE,
        50,
        TRUE
    ),

    (
        'MANUAL_USAGE',
        'Manual Usage',
        'Ingredient manually consumed outside a POS sale.',
        -1,
        TRUE,
        60,
        TRUE
    ),

    (
        'SALE_USAGE',
        'Sale Usage',
        'Ingredient consumed automatically from a completed sale.',
        -1,
        FALSE,
        70,
        TRUE
    ),

    (
        'RETURN_IN',
        'Return In',
        'Stock returned into inventory.',
        1,
        TRUE,
        80,
        TRUE
    ),

    (
        'RETURN_OUT',
        'Return Out',
        'Stock returned to a supplier or otherwise removed.',
        -1,
        TRUE,
        90,
        TRUE
    )

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- INVENTORY PERMISSIONS
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description,
    is_active
)
SELECT
    mod.id,
    permission.code,
    permission.name,
    permission.description,
    TRUE
FROM lt_modules_mod mod

CROSS JOIN (
    VALUES

        (
            'VIEW',
            'View Inventory',
            'View inventory balances and movement history.'
        ),

        (
            'RECORD_OPENING_STOCK',
            'Record Opening Stock',
            'Record initial ingredient stock balances.'
        ),

        (
            'STOCK_IN',
            'Stock In',
            'Record permitted stock increases.'
        ),

        (
            'STOCK_OUT',
            'Stock Out',
            'Record permitted stock usage or removal.'
        ),

        (
            'ADJUST_STOCK',
            'Adjust Stock',
            'Record manual inventory adjustments.'
        ),

        (
            'RECORD_WASTE',
            'Record Waste',
            'Record ingredient waste, damage or spoilage.'
        )

) AS permission(
    code,
    name,
    description
)

WHERE mod.code = 'INVENTORY'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- MAKE SURE ADMIN HAS INVENTORY MODULE
-- ============================================================

INSERT INTO dt_role_modules_rm (
    role_id,
    module_id
)
SELECT
    rl.id,
    mod.id
FROM lt_roles_rl rl

CROSS JOIN lt_modules_mod mod

WHERE rl.code = 'ADMIN'
  AND mod.code = 'INVENTORY'

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;


-- ============================================================
-- ASSIGN ALL INVENTORY PERMISSIONS TO ADMIN
-- ============================================================

INSERT INTO dt_role_module_permissions_rmp (
    role_module_id,
    module_permission_id
)
SELECT
    rm.id,
    mp.id
FROM dt_role_modules_rm rm

INNER JOIN lt_roles_rl rl
    ON rl.id = rm.role_id

INNER JOIN lt_modules_mod mod
    ON mod.id = rm.module_id

INNER JOIN lt_module_permissions_mp mp
    ON mp.module_id = mod.id

WHERE rl.code = 'ADMIN'
  AND mod.code = 'INVENTORY'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;