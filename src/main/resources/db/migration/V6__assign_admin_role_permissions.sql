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
  AND mod.is_active = TRUE;
  
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
INNER JOIN lt_module_permissions_mp mp
    ON mp.module_id = rm.module_id
WHERE rl.code = 'ADMIN'
  AND mp.is_active = TRUE;