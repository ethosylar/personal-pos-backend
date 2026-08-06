package site.ethosylar.pos.common.exception;

import site.ethosylar.pos.common.error.ApiErrorCode;

public class DuplicateResourceException extends ApplicationException {

	public DuplicateResourceException(String message) {

		super(ApiErrorCode.DUPLICATE_RESOURCE, message);
	}
}