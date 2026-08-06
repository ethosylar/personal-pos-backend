package site.ethosylar.pos.common.exception;

import site.ethosylar.pos.common.error.ApiErrorCode;

public class ResourceNotFoundException extends ApplicationException {

	public ResourceNotFoundException(String message) {

		super(ApiErrorCode.RESOURCE_NOT_FOUND, message);
	}
}