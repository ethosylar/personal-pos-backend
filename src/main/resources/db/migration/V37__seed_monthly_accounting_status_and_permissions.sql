-- ============================================================
-- ACCOUNTING STATUSES
-- ============================================================

INSERT INTO st_monthly_accounting_status_mas (
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
    'Monthly accounting has been calculated but may still be refreshed.',
    10,
    TRUE
),

(
    'POSTED',
    'Posted',
    'Monthly accounting is finalized.',
    20,
    TRUE
),

(
    'VOIDED',
    'Voided',
    'Monthly accounting statement has been voided.',
    30,
    TRUE
)

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- ACCOUNTING MODULE
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
    'ACCOUNTING',
    'Monthly Accounting',
    'Monthly business financial summary and profitability.',
    '/accounting',
    'account_balance',
    90,
    TRUE
)

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- ACCOUNTING PERMISSIONS
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
        'View Accounting',
        'View monthly accounting records.'
    ),

    (
        'GENERATE',
        'Generate Accounting',
        'Generate a monthly accounting statement.'
    ),

    (
        'REFRESH',
        'Refresh Accounting',
        'Recalculate a draft monthly accounting statement.'
    ),

    (
        'POST',
        'Post Accounting',
        'Finalize a monthly accounting statement.'
    ),

    (
        'VOID',
        'Void Accounting',
        'Void a posted monthly accounting statement.'
    )

) AS p(
    code,
    name,
    description
)

WHERE mod.code = 'ACCOUNTING'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- ADMIN ACCESS
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
  AND mod.code = 'ACCOUNTING'

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;


-- ============================================================
-- ADMIN ACCOUNTING PERMISSIONS
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
  AND mod.code = 'ACCOUNTING'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;