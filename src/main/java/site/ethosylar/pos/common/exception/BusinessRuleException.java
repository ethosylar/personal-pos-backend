package site.ethosylar.pos.common.exception;

import site.ethosylar.pos.common.error.ApiErrorCode;

public class BusinessRuleException extends ApplicationException {

	public BusinessRuleException(String message) {

		super(ApiErrorCode.BUSINESS_RULE_VIOLATION, message);
	}

	public BusinessRuleException(ApiErrorCode errorCode, String message) {

		super(errorCode, message);
	}
}