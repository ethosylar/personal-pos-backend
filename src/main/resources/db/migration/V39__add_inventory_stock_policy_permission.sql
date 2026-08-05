-- ============================================================
-- INVENTORY STOCK POLICY PERMISSION
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
    'MANAGE_STOCK_POLICY',
    'Manage Stock Policy',
    'Configure ingredient reorder levels and stock targets.',
    TRUE

FROM lt_modules_mod mod

WHERE mod.code = 'INVENTORY'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- ADMIN PERMISSION
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

  AND mod.code = 'INVENTORY'

  AND mp.code = 'MANAGE_STOCK_POLICY'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;