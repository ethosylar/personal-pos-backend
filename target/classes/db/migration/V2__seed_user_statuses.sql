INSERT INTO st_user_status_us (
    code,
    name,
    description,
    is_active
)
VALUES
    (
        'ACTIVE',
        'Active',
        'User account is active and allowed to access the system.',
        TRUE
    ),
    (
        'INACTIVE',
        'Inactive',
        'User account is inactive and cannot access the system.',
        TRUE
    ),
    (
        'LOCKED',
        'Locked',
        'User account has been locked due to security or login restrictions.',
        TRUE
    ),
    (
        'SUSPENDED',
        'Suspended',
        'User account has been administratively suspended.',
        TRUE
    );