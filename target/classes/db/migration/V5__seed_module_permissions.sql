-- ============================================================
-- DASHBOARD
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT
    id,
    'VIEW',
    'View Dashboard',
    'Allow access to the dashboard.'
FROM lt_modules_mod
WHERE code = 'DASHBOARD';


-- ============================================================
-- USER MANAGEMENT
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW', 'View Users', 'View system users.'),
        ('CREATE', 'Create User', 'Create new system users.'),
        ('UPDATE', 'Update User', 'Modify system user information.'),
        ('DELETE', 'Delete User', 'Delete system users.'),
        ('RESET_PASSWORD', 'Reset Password', 'Reset another user''s password.'),
        ('ASSIGN_ROLE', 'Assign Role', 'Assign or remove roles from users.'),
        ('CHANGE_STATUS', 'Change User Status', 'Activate, deactivate, lock or suspend a user.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'USER_MANAGEMENT';


-- ============================================================
-- ROLE AND PERMISSION MANAGEMENT
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW', 'View Roles', 'View roles and assigned permissions.'),
        ('CREATE', 'Create Role', 'Create new roles.'),
        ('UPDATE', 'Update Role', 'Modify role information.'),
        ('DELETE', 'Delete Role', 'Delete non-system roles.'),
        ('ASSIGN_MODULE', 'Assign Module', 'Assign or remove modules from roles.'),
        ('ASSIGN_PERMISSION', 'Assign Permission', 'Assign or remove module permissions from roles.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'ROLE_PERMISSION_MANAGEMENT';


-- ============================================================
-- PRODUCT
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW', 'View Products', 'View products.'),
        ('CREATE', 'Create Product', 'Create new products.'),
        ('UPDATE', 'Update Product', 'Modify product information.'),
        ('DELETE', 'Delete Product', 'Delete products.'),
        ('CHANGE_PRICE', 'Change Product Price', 'Modify product selling prices.'),
        ('IMPORT', 'Import Products', 'Bulk import product information.'),
        ('EXPORT', 'Export Products', 'Export product information.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'PRODUCT';


-- ============================================================
-- CATEGORY
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW', 'View Categories', 'View product categories.'),
        ('CREATE', 'Create Category', 'Create product categories.'),
        ('UPDATE', 'Update Category', 'Modify product categories.'),
        ('DELETE', 'Delete Category', 'Delete product categories.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'CATEGORY';


-- ============================================================
-- INVENTORY
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW', 'View Inventory', 'View inventory balances.'),
        ('STOCK_IN', 'Stock In', 'Record incoming stock.'),
        ('STOCK_OUT', 'Stock Out', 'Record outgoing stock.'),
        ('ADJUST_STOCK', 'Adjust Stock', 'Perform manual inventory adjustments.'),
        ('VIEW_MOVEMENT', 'View Stock Movement', 'View inventory movement history.'),
        ('EXPORT', 'Export Inventory', 'Export inventory information.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'INVENTORY';


-- ============================================================
-- POINT OF SALE
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('ACCESS', 'Access POS', 'Access the POS cashier interface.'),
        ('CREATE_SALE', 'Create Sale', 'Create and complete POS transactions.'),
        ('APPLY_DISCOUNT', 'Apply Discount', 'Apply discounts to POS transactions.'),
        ('HOLD_SALE', 'Hold Sale', 'Temporarily hold an active transaction.'),
        ('RESUME_SALE', 'Resume Sale', 'Resume a previously held transaction.'),
        ('CANCEL_SALE', 'Cancel Sale', 'Cancel an unfinished POS transaction.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'POS';


-- ============================================================
-- SALES
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW', 'View Sales', 'View sales transactions.'),
        ('VIEW_DETAIL', 'View Sale Detail', 'View detailed sales information.'),
        ('VOID', 'Void Sale', 'Void an eligible sales transaction.'),
        ('REFUND', 'Refund Sale', 'Perform a refund against a sales transaction.'),
        ('REPRINT_RECEIPT', 'Reprint Receipt', 'Reprint a sales receipt.'),
        ('EXPORT', 'Export Sales', 'Export sales transaction information.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'SALES';


-- ============================================================
-- PAYMENT
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW', 'View Payments', 'View payment records.'),
        ('PROCESS', 'Process Payment', 'Process POS payments.'),
        ('VOID', 'Void Payment', 'Void eligible payments.'),
        ('REFUND', 'Refund Payment', 'Refund eligible payments.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'PAYMENT';


-- ============================================================
-- CUSTOMER
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW', 'View Customers', 'View customer information.'),
        ('CREATE', 'Create Customer', 'Create customer records.'),
        ('UPDATE', 'Update Customer', 'Modify customer information.'),
        ('DELETE', 'Delete Customer', 'Delete customer records.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'CUSTOMER';


-- ============================================================
-- REPORT
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW_SALES', 'View Sales Report', 'View sales reports.'),
        ('VIEW_INVENTORY', 'View Inventory Report', 'View inventory reports.'),
        ('VIEW_FINANCIAL', 'View Financial Report', 'View financial reports.'),
        ('EXPORT', 'Export Reports', 'Export reporting data.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'REPORT';


-- ============================================================
-- SETTINGS
-- ============================================================

INSERT INTO lt_module_permissions_mp (
    module_id,
    code,
    name,
    description
)
SELECT id, permission_code, permission_name, permission_description
FROM lt_modules_mod
CROSS JOIN (
    VALUES
        ('VIEW', 'View Settings', 'View system configuration.'),
        ('UPDATE', 'Update Settings', 'Modify system configuration.')
) AS permission(permission_code, permission_name, permission_description)
WHERE code = 'SETTINGS';