-- ============================================================
-- EXPENSE CATEGORIES
-- ============================================================

INSERT INTO lt_expense_categories_ec (
    code,
    name,
    description,
    expense_class,
    display_order,
    is_active
)
VALUES

    (
        'RENT',
        'Rent',
        'Shop, office or premises rental.',
        'FIXED',
        10,
        TRUE
    ),

    (
        'UTILITIES',
        'Utilities',
        'Electricity, water and utility expenses.',
        'VARIABLE',
        20,
        TRUE
    ),

    (
        'INTERNET',
        'Internet & Telecommunications',
        'Internet, phone and communication expenses.',
        'FIXED',
        30,
        TRUE
    ),

    (
        'SOFTWARE',
        'Software & Subscriptions',
        'Software subscriptions and online services.',
        'FIXED',
        40,
        TRUE
    ),

    (
        'MARKETING',
        'Marketing & Advertising',
        'Advertising, promotion and marketing costs.',
        'VARIABLE',
        50,
        TRUE
    ),

    (
        'REPAIRS',
        'Repairs & Maintenance',
        'Equipment and premises maintenance.',
        'VARIABLE',
        60,
        TRUE
    ),

    (
        'CLEANING',
        'Cleaning & Sanitation',
        'Cleaning materials and cleaning services.',
        'VARIABLE',
        70,
        TRUE
    ),

    (
        'BANK_CHARGES',
        'Bank Charges',
        'Banking, merchant and transaction charges.',
        'VARIABLE',
        80,
        TRUE
    ),

    (
        'TRANSPORT',
        'Transport',
        'Delivery, transport and travel expenses.',
        'VARIABLE',
        90,
        TRUE
    ),

    (
        'OTHER',
        'Other Expenses',
        'Other operating expenses.',
        'OTHER',
        100,
        TRUE
    )

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- EXPENSE STATUSES
-- ============================================================

INSERT INTO st_expense_status_es (
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
        'Expense is being prepared and may still be modified.',
        10,
        TRUE
    ),

    (
        'POSTED',
        'Posted',
        'Expense is finalized and included in financial reporting.',
        20,
        TRUE
    ),

    (
        'VOIDED',
        'Voided',
        'Expense has been voided and excluded from financial reporting.',
        30,
        TRUE
    )

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- EXPENSE MODULE
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
    'EXPENSE',
    'Expenses',
    'Manage operating expenses and overheads.',
    '/expenses',
    'receipt_long',
    75,
    TRUE
)
ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- EXPENSE PERMISSIONS
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
            'View Expenses',
            'View expense records and history.'
        ),

        (
            'CREATE',
            'Create Expense',
            'Create draft expenses.'
        ),

        (
            'UPDATE',
            'Update Expense',
            'Update draft expenses.'
        ),

        (
            'POST',
            'Post Expense',
            'Finalize expenses for financial reporting.'
        ),

        (
            'VOID',
            'Void Expense',
            'Void previously posted expenses.'
        )

) AS p(
    code,
    name,
    description
)

WHERE mod.code = 'EXPENSE'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- ADMIN MODULE ACCESS
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
  AND mod.code = 'EXPENSE'

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;


-- ============================================================
-- ADMIN EXPENSE PERMISSIONS
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
  AND mod.code = 'EXPENSE'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;