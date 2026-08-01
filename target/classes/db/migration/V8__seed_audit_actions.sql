INSERT INTO lt_audit_actions_aa (
    code,
    name,
    description,
    is_active
)
VALUES
    (
        'LOGIN_SUCCESS',
        'Login Success',
        'A user successfully authenticated.',
        TRUE
    ),
    (
        'LOGIN_FAILURE',
        'Login Failure',
        'A user authentication attempt failed.',
        TRUE
    ),
    (
        'CREATE',
        'Create',
        'A new record was created.',
        TRUE
    ),
    (
        'UPDATE',
        'Update',
        'An existing record was modified.',
        TRUE
    ),
    (
        'DELETE',
        'Delete',
        'A record was deleted.',
        TRUE
    ),
    (
        'STATUS_CHANGE',
        'Status Change',
        'The status of a record or user was changed.',
        TRUE
    ),
    (
        'ROLE_ASSIGNMENT',
        'Role Assignment',
        'Roles assigned to a user were changed.',
        TRUE
    ),
    (
        'MODULE_ASSIGNMENT',
        'Module Assignment',
        'Modules assigned to a role were changed.',
        TRUE
    ),
    (
        'PERMISSION_ASSIGNMENT',
        'Permission Assignment',
        'Permissions assigned to a role module were changed.',
        TRUE
    ),
    (
        'PASSWORD_CHANGE',
        'Password Change',
        'A user changed their own password.',
        TRUE
    ),
    (
        'PASSWORD_RESET',
        'Password Reset',
        'An administrator reset another user''s password.',
        TRUE
    );