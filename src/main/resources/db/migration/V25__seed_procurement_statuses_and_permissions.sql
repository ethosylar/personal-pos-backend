-- ============================================================
-- PURCHASE ORDER STATUSES
-- ============================================================

INSERT INTO st_purchase_order_status_pos (
    code,
    name,
    description,
    display_order,
    is_active
)
VALUES

    (
        'DRAFT',
        'Draft',
        'Purchase order is being prepared and may still be modified.',
        10,
        TRUE
    ),

    (
        'ORDERED',
        'Ordered',
        'Purchase order has been placed with the supplier.',
        20,
        TRUE
    ),

    (
        'PARTIALLY_RECEIVED',
        'Partially Received',
        'Some but not all ordered goods have been received.',
        30,
        TRUE
    ),

    (
        'RECEIVED',
        'Received',
        'All ordered goods have been received.',
        40,
        TRUE
    ),

    (
        'CANCELLED',
        'Cancelled',
        'Purchase order has been cancelled.',
        50,
        TRUE
    )

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- PURCHASING MODULE
-- ============================================================

INSERT INTO lt_modules_mod (
    code,
    name,
    description,
    route,
    icon,
    display_order,
    is_active
)
VALUES (
    'PURCHASING',
    'Purchasing',
    'Manage purchase orders and goods receipts.',
    '/purchasing',
    'shopping_cart',
    68,
    TRUE
)
ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- PURCHASING PERMISSIONS
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
    p.code,
    p.name,
    p.description,
    TRUE
FROM lt_modules_mod mod

CROSS JOIN (
    VALUES

        (
            'VIEW',
            'View Purchasing',
            'View purchase orders and goods receipts.'
        ),

        (
            'CREATE_PO',
            'Create Purchase Order',
            'Create purchase orders.'
        ),

        (
            'UPDATE_PO',
            'Update Purchase Order',
            'Update draft purchase orders.'
        ),

        (
            'PLACE_ORDER',
            'Place Purchase Order',
            'Mark a draft purchase order as ordered.'
        ),

        (
            'CANCEL_PO',
            'Cancel Purchase Order',
            'Cancel eligible purchase orders.'
        ),

        (
            'RECEIVE_GOODS',
            'Receive Goods',
            'Create goods receipts and increase inventory.'
        )

) AS p(
    code,
    name,
    description
)

WHERE mod.code = 'PURCHASING'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- ASSIGN PURCHASING TO ADMIN
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
  AND mod.code = 'PURCHASING'

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;


-- ============================================================
-- ASSIGN PURCHASING PERMISSIONS TO ADMIN
-- ============================================================

INSERT INTO dt_role_module_permissions_rmp (
    role_module_id,
    module_permission_id
)
SELECT
    rm.id,
    mp.id

FROM dt_role_modules_rm rm

JOIN lt_roles_rl rl
    ON rl.id = rm.role_id

JOIN lt_modules_mod mod
    ON mod.id = rm.module_id

JOIN lt_module_permissions_mp mp
    ON mp.module_id = mod.id

WHERE rl.code = 'ADMIN'
  AND mod.code = 'PURCHASING'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;