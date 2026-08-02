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
            'CHANGE_STATUS',
            'Change Product Status',
            'Activate or deactivate products.'
        ),

        (
            'CHANGE_AVAILABILITY',
            'Change Product Availability',
            'Control whether a product is currently available for sale.'
        )

) AS permission(
    code,
    name,
    description
)

WHERE mod.code = 'PRODUCT'

ON CONFLICT (
    module_id,
    code
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

INNER JOIN lt_roles_rl rl
    ON rl.id = rm.role_id

INNER JOIN lt_modules_mod mod
    ON mod.id = rm.module_id

INNER JOIN lt_module_permissions_mp mp
    ON mp.module_id = mod.id

WHERE rl.code = 'ADMIN'
  AND mod.code = 'PRODUCT'

ON CONFLICT (
    role_module_id,
    module_permission_id
)
DO NOTHING;