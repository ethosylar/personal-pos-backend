INSERT INTO lt_modules_mod (
    code,
    name,
    description,
    route,
    icon,
    display_order,
    is_active
)
VALUES
    (
        'DASHBOARD',
        'Dashboard',
        'Main POS dashboard and operational overview.',
        '/dashboard',
        'dashboard',
        10,
        TRUE
    ),
    (
        'USER_MANAGEMENT',
        'User Management',
        'Manage system users and user accounts.',
        '/administration/users',
        'users',
        20,
        TRUE
    ),
    (
        'ROLE_PERMISSION_MANAGEMENT',
        'Role & Permission Management',
        'Manage roles, module assignments and permissions.',
        '/administration/roles',
        'shield',
        30,
        TRUE
    ),
    (
        'PRODUCT',
        'Product Management',
        'Manage products, pricing and product information.',
        '/products',
        'package',
        40,
        TRUE
    ),
    (
        'CATEGORY',
        'Category Management',
        'Manage product categories.',
        '/categories',
        'category',
        50,
        TRUE
    ),
    (
        'INVENTORY',
        'Inventory Management',
        'Manage stock balances and inventory movements.',
        '/inventory',
        'inventory',
        60,
        TRUE
    ),
    (
        'POS',
        'Point of Sale',
        'Main cashier and transaction interface.',
        '/pos',
        'shopping-cart',
        70,
        TRUE
    ),
    (
        'SALES',
        'Sales Management',
        'Manage and review completed sales transactions.',
        '/sales',
        'receipt',
        80,
        TRUE
    ),
    (
        'PAYMENT',
        'Payment Management',
        'Manage and review payment information.',
        '/payments',
        'payment',
        90,
        TRUE
    ),
    (
        'CUSTOMER',
        'Customer Management',
        'Manage customer information.',
        '/customers',
        'person',
        100,
        TRUE
    ),
    (
        'REPORT',
        'Reports',
        'View and generate operational and financial reports.',
        '/reports',
        'analytics',
        110,
        TRUE
    ),
    (
        'SETTINGS',
        'System Settings',
        'Manage system configuration and POS settings.',
        '/settings',
        'settings',
        120,
        TRUE
    );