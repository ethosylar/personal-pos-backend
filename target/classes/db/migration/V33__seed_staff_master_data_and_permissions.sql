-- ============================================================
-- STAFF STATUS
-- ============================================================

INSERT INTO st_staff_status_sfs (
    code,
    name,
    description,
    display_order,
    is_active
)
VALUES

(
    'ACTIVE',
    'Active',
    'Staff member is currently employed.',
    10,
    TRUE
),

(
    'INACTIVE',
    'Inactive',
    'Staff member is temporarily inactive.',
    20,
    TRUE
),

(
    'TERMINATED',
    'Terminated',
    'Staff employment has ended.',
    30,
    TRUE
)

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- PAY TYPES
-- ============================================================

INSERT INTO lt_salary_pay_types_spt (
    code,
    name,
    description,
    display_order,
    is_active
)
VALUES

(
    'MONTHLY',
    'Monthly',
    'Fixed monthly salary.',
    10,
    TRUE
),

(
    'DAILY',
    'Daily',
    'Salary calculated using a daily rate.',
    20,
    TRUE
),

(
    'HOURLY',
    'Hourly',
    'Salary calculated using an hourly rate.',
    30,
    TRUE
)

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- STAFF MODULE
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
    'STAFF',
    'Staff',
    'Manage staff records and salary history.',
    '/staff',
    'people',
    80,
    TRUE
)

ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- STAFF PERMISSIONS
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
        'View Staff',
        'View staff records.'
    ),

    (
        'CREATE',
        'Create Staff',
        'Create staff records.'
    ),

    (
        'UPDATE',
        'Update Staff',
        'Update staff records.'
    ),

    (
        'MANAGE_SALARY',
        'Manage Salary',
        'Manage staff salary rate history.'
    )

) AS p(
    code,
    name,
    description
)

WHERE mod.code = 'STAFF'

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
  AND mod.code = 'STAFF'

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;


-- ============================================================
-- ADMIN STAFF PERMISSIONS
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
  AND mod.code = 'STAFF'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;