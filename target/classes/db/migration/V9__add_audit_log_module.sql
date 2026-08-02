-- ============================================================
-- AUDIT LOG MODULE
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
    'AUDIT_LOG',
    'Audit Log',
    'View system activity and audit history.',
    '/administration/audit-logs',
    'history',
    130,
    TRUE
);


-- ============================================================
-- AUDIT LOG PERMISSIONS
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT
    mod.id,
    permission.code,
    permission.name,
    permission.description
FROM lt_modules_mod mod
CROSS JOIN (
    VALUES
        (
            'VIEW',
            'View Audit Logs',
            'View system audit history.'
        ),
        (
            'EXPORT',
            'Export Audit Logs',
            'Export system audit history.'
        )
) AS permission(code, name, description)
WHERE mod.code = 'AUDIT_LOG';


-- ============================================================
-- ASSIGN AUDIT MODULE TO ADMIN
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
  AND mod.code = 'AUDIT_LOG'
ON CONFLICT (role_id, module_id)
DO NOTHING;


-- ============================================================
-- ASSIGN ALL AUDIT PERMISSIONS TO ADMIN
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
  AND mod.code = 'AUDIT_LOG'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;