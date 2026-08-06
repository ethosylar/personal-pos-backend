package site.ethosylar.pos.common.exception;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import site.ethosylar.pos.common.error.ApiErrorCode;
import site.ethosylar.pos.common.error.ApiErrorResponse;
import site.ethosylar.pos.common.error.ApiFieldError;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

		return build(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {

		return build(HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<ApiErrorResponse> handleBusinessRule(BusinessRuleException ex, HttpServletRequest request) {

		return build(HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		List<ApiFieldError> fields = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> ApiFieldError.builder()

						.field(error.getField())

						.code(error.getCode())

						.message(error.getDefaultMessage())

						.build())
				.toList();

		return build(HttpStatus.BAD_REQUEST, ApiErrorCode.VALIDATION_FAILED, "Request validation failed.", request,
				fields);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {

		return build(HttpStatus.FORBIDDEN, ApiErrorCode.ACCESS_DENIED,
				"You do not have permission to perform this action.", request, List.of());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {

		return build(HttpStatus.NOT_FOUND, ApiErrorCode.RESOURCE_NOT_FOUND, "API endpoint not found.", request,
				List.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {

		/*
		 * Log ex internally.
		 *
		 * Do NOT expose stack traces, SQL errors or implementation details to the
		 * client.
		 */

		return build(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_ERROR,
				"An unexpected server error occurred.", request, List.of());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
			HttpServletRequest request) {

		return build(HttpStatus.CONFLICT, ApiErrorCode.DATA_INTEGRITY_VIOLATION,
				"The requested operation violates a database constraint.", request, List.of());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex,
			HttpServletRequest request) {

		return build(HttpStatus.UNAUTHORIZED, ApiErrorCode.INVALID_CREDENTIALS, "Invalid username or password.",
				request, List.of());
	}

	@ExceptionHandler({ DisabledException.class, LockedException.class })
	public ResponseEntity<ApiErrorResponse> handleUnavailableAccount(RuntimeException ex, HttpServletRequest request) {

		return build(HttpStatus.UNAUTHORIZED, ApiErrorCode.INVALID_CREDENTIALS, "Invalid username or password.",
				request, List.of());
	}

	private ResponseEntity<ApiErrorResponse> build(HttpStatus status, ApiErrorCode code, String message,
			HttpServletRequest request, List<ApiFieldError> fieldErrors) {

		ApiErrorResponse response = ApiErrorResponse.builder()

				.timestamp(OffsetDateTime.now())

				.status(status.value())

				.error(status.getReasonPhrase())

				.code(code.name())

				.message(message)

				.path(request.getRequestURI())

				.fieldErrors(fieldErrors)

				.traceId(null)

				.build();

		return ResponseEntity.status(status).body(response);
	}
}