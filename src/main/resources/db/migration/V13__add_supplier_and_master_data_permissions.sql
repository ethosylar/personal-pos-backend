-- ============================================================
-- SUPPLIER MODULE
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
    'SUPPLIER',
    'Supplier Management',
    'Manage suppliers and supplier master data.',
    '/suppliers',
    'local_shipping',
    65,
    TRUE
)
ON CONFLICT (code)
DO NOTHING;


-- ============================================================
-- CATEGORY PERMISSIONS
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
            'View Categories',
            'View product and ingredient categories.'
        ),

        (
            'CREATE',
            'Create Categories',
            'Create product and ingredient categories.'
        ),

        (
            'UPDATE',
            'Update Categories',
            'Update product and ingredient categories.'
        ),

        (
            'CHANGE_STATUS',
            'Change Category Status',
            'Activate or deactivate categories.'
        )

) AS permission(
    code,
    name,
    description
)

WHERE mod.code = 'CATEGORY'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- SUPPLIER PERMISSIONS
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
            'View Suppliers',
            'View supplier master data.'
        ),

        (
            'CREATE',
            'Create Suppliers',
            'Create suppliers.'
        ),

        (
            'UPDATE',
            'Update Suppliers',
            'Update supplier information.'
        ),

        (
            'CHANGE_STATUS',
            'Change Supplier Status',
            'Activate or deactivate suppliers.'
        )

) AS permission(
    code,
    name,
    description
)

WHERE mod.code = 'SUPPLIER'

ON CONFLICT (
    module_id,
    code
)
DO NOTHING;


-- ============================================================
-- ASSIGN MODULES TO ADMIN
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
  AND mod.code IN (
      'CATEGORY',
      'SUPPLIER'
  )

ON CONFLICT (
    role_id,
    module_id
)
DO NOTHING;


-- ============================================================
-- ASSIGN ALL NEW PERMISSIONS TO ADMIN
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
  AND mod.code IN (
      'CATEGORY',
      'SUPPLIER'
  )

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;