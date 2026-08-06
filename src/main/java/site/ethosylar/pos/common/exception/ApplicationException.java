package site.ethosylar.pos.common.exception;

import site.ethosylar.pos.common.error.ApiErrorCode;

public abstract class ApplicationException extends RuntimeException {

	private final ApiErrorCode errorCode;

	protected ApplicationException(ApiErrorCode errorCode, String message) {

		super(message);

		this.errorCode = errorCode;
	}

	public ApiErrorCode getErrorCode() {
		return errorCode;
	}
}