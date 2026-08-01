INSERT INTO lt_roles_rl (
    code,
    name,
    description,
    is_system,
    is_active
)
VALUES
    (
        'ADMIN',
        'Administrator',
        'Full system administration access.',
        TRUE,
        TRUE
    ),
    (
        'MANAGER',
        'Manager',
        'Management access including POS operations, inventory, reporting and administration functions.',
        TRUE,
        TRUE
    ),
    (
        'CASHIER',
        'Cashier',
        'Point-of-sale transaction and cashier operational access.',
        TRUE,
        TRUE
    );