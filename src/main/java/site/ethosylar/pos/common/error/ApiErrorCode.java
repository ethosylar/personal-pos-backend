package site.ethosylar.pos.common.error;

public enum ApiErrorCode {

    // Generic
    INTERNAL_ERROR,
    INVALID_REQUEST,
    VALIDATION_FAILED,
    RESOURCE_NOT_FOUND,
    DUPLICATE_RESOURCE,
    BUSINESS_RULE_VIOLATION,
    ACCESS_DENIED,
    UNAUTHORIZED,
    DATA_INTEGRITY_VIOLATION,

    // Authentication
    INVALID_CREDENTIALS,
    TOKEN_INVALID,
    TOKEN_EXPIRED,

    // Inventory
    INSUFFICIENT_STOCK,

    // Accounting
    ACCOUNTING_PERIOD_CLOSED,

    // Procurement
    INVALID_PURCHASE_ORDER_STATE,

    // Sale
    INVALID_SALE_STATE,

    // Payroll
    INVALID_PAYROLL_STATE,
    
    // Rate Limiting
    RATE_LIMIT_EXCEEDED
}