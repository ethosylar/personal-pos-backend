-- ============================================================
-- SALE STATUSES
-- ============================================================

INSERT INTO st_sale_status_ss (
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
        'Sale is currently being prepared.',
        10,
        TRUE
    ),

    (
        'COMPLETED',
        'Completed',
        'Sale has been successfully completed and inventory posted.',
        20,
        TRUE
    ),

    (
        'VOIDED',
        'Voided',
        'Sale has been voided.',
        30,
        TRUE
    )

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- PAYMENT METHODS
-- ============================================================

INSERT INTO lt_payment_methods_pm (
    code,
    name,
    description,
    display_order,
    is_active
)
VALUES

    (
        'CASH',
        'Cash',
        'Cash payment.',
        10,
        TRUE
    ),

    (
        'CARD',
        'Card',
        'Debit or credit card payment.',
        20,
        TRUE
    ),

    (
        'QR',
        'QR Payment',
        'QR-based electronic payment.',
        30,
        TRUE
    ),

    (
        'EWALLET',
        'E-Wallet',
        'Electronic wallet payment.',
        40,
        TRUE
    ),

    (
        'BANK_TRANSFER',
        'Bank Transfer',
        'Direct bank transfer.',
        50,
        TRUE
    )

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- PAYMENT STATUS
-- ============================================================

INSERT INTO st_payment_status_ps (
    code,
    name,
    description,
    display_order,
    is_active
)
VALUES

    (
        'PENDING',
        'Pending',
        'Payment has not yet been confirmed.',
        10,
        TRUE
    ),

    (
        'COMPLETED',
        'Completed',
        'Payment has been successfully completed.',
        20,
        TRUE
    ),

    (
        'FAILED',
        'Failed',
        'Payment attempt failed.',
        30,
        TRUE
    ),

    (
        'VOIDED',
        'Voided',
        'Payment was voided.',
        40,
        TRUE
    ),

    (
        'REFUNDED',
        'Refunded',
        'Payment was refunded.',
        50,
        TRUE
    )

ON CONFLICT (code)
DO NOTHING;

-- ============================================================
-- POS PERMISSIONS
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
            'CREATE_SALE',
            'Create Sale',
            'Create and manage a POS sale.'
        ),
        (
            'COMPLETE_SALE',
            'Complete Sale',
            'Complete checkout and post inventory usage.'
        )
) AS p(
    code,
    name,
    description
)

WHERE mod.code = 'POS'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- SALES PERMISSIONS
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
            'View Sales',
            'View completed and historical sales.'
        ),
        (
            'VOID_SALE',
            'Void Sale',
            'Void eligible sales and reverse stock usage.'
        )
) AS p(
    code,
    name,
    description
)

WHERE mod.code = 'SALES'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- PAYMENT PERMISSIONS
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
            'View Payments',
            'View sale payment information.'
        ),
        (
            'RECORD_PAYMENT',
            'Record Payment',
            'Record payments against sales.'
        )
) AS p(
    code,
    name,
    description
)

WHERE mod.code = 'PAYMENT'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;

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
  AND mod.code IN (
      'POS',
      'SALES',
      'PAYMENT'
  )

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;

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
  AND mod.code IN (
      'POS',
      'SALES',
      'PAYMENT'
  )

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;