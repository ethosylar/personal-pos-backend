-- ============================================================
-- MONTHLY STAFF COST STATUSES
-- ============================================================

INSERT INTO st_staff_monthly_cost_status_smcs (
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
    'Monthly staff cost may still be modified.',
    10,
    TRUE
),

(
    'POSTED',
    'Posted',
    'Monthly staff cost is finalized for accounting.',
    20,
    TRUE
),

(
    'VOIDED',
    'Voided',
    'Monthly staff cost has been voided.',
    30,
    TRUE
)

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- PAYROLL MODULE
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
    'PAYROLL',
    'Staff Salary',
    'Manage monthly staff salary and employment costs.',
    '/payroll',
    'payments',
    85,
    TRUE
)

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- PAYROLL PERMISSIONS
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
        'View Payroll',
        'View monthly staff cost records.'
    ),

    (
        'CREATE',
        'Create Payroll',
        'Create monthly staff cost records.'
    ),

    (
        'UPDATE',
        'Update Payroll',
        'Update draft monthly staff costs.'
    ),

    (
        'POST',
        'Post Payroll',
        'Finalize monthly staff costs.'
    ),

    (
        'VOID',
        'Void Payroll',
        'Void posted monthly staff costs.'
    )

) AS p(
    code,
    name,
    description
)

WHERE mod.code = 'PAYROLL'

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
  AND mod.code = 'PAYROLL'

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;


-- ============================================================
-- ADMIN PAYROLL PERMISSIONS
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
  AND mod.code = 'PAYROLL'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;