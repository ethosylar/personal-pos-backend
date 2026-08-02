-- ============================================================
-- RECIPE MODULE
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
    'RECIPE',
    'Recipe & Costing',
    'Manage product recipes, versions and recipe costing.',
    '/recipes',
    'restaurant_menu',
    58,
    TRUE
)
ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- RECIPE PERMISSIONS
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
            'View Recipes',
            'View product recipes and costing information.'
        ),

        (
            'CREATE',
            'Create Recipes',
            'Create new recipe versions.'
        ),

        (
            'UPDATE',
            'Update Recipes',
            'Update draft recipes and recipe ingredients.'
        ),

        (
            'ACTIVATE',
            'Activate Recipe',
            'Activate a recipe version for a product.'
        )

) AS permission(
    code,
    name,
    description
)

WHERE mod.code = 'RECIPE'

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
  AND mod.code = 'RECIPE'

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;


-- ============================================================
-- ASSIGN ALL RECIPE PERMISSIONS TO ADMIN
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
  AND mod.code = 'RECIPE'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;


-- ============================================================
-- RECIPE ACTIVATION AUDIT ACTION
-- ============================================================

INSERT INTO lt_audit_actions_aa (
    code,
    name,
    description,
    is_active
)
VALUES (
    'RECIPE_ACTIVATE',
    'Recipe Activate',
    'A recipe version was activated for a product.',
    TRUE
)
ON CONFLICT (code)
DO NOTHING;