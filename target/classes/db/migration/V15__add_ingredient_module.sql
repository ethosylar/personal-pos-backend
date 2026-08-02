-- ============================================================
-- INGREDIENT MODULE
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
    'INGREDIENT',
    'Ingredient Management',
    'Manage ingredients, supplier options and ingredient costing.',
    '/ingredients',
    'inventory_2',
    55,
    TRUE
)
ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- INGREDIENT PERMISSIONS
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
            'View Ingredients',
            'View ingredient information.'
        ),

        (
            'CREATE',
            'Create Ingredients',
            'Create ingredients.'
        ),

        (
            'UPDATE',
            'Update Ingredients',
            'Update ingredient information.'
        ),

        (
            'CHANGE_STATUS',
            'Change Ingredient Status',
            'Activate or deactivate ingredients.'
        ),

        (
            'MANAGE_SUPPLIERS',
            'Manage Ingredient Suppliers',
            'Manage suppliers and purchase costing for ingredients.'
        )

) AS permission(
    code,
    name,
    description
)

WHERE mod.code = 'INGREDIENT'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- ASSIGN MODULE TO ADMIN
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
  AND mod.code = 'INGREDIENT'

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;


-- ============================================================
-- ASSIGN PERMISSIONS TO ADMIN
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
  AND mod.code = 'INGREDIENT'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;