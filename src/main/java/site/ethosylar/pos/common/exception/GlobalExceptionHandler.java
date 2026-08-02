package site.ethosylar.pos.common.exception;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import jakarta.servlet.http.HttpServletRequest;
import site.ethosylar.pos.common.response.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {

		return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<ApiErrorResponse> handleBusinessRule(BusinessRuleException ex, HttpServletRequest request) {

		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		Map<String, String> errors = new LinkedHashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed.", request.getRequestURI(), errors);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
			HttpServletRequest request) {

		return buildResponse(HttpStatus.CONFLICT, "The requested operation violates a database constraint.",
				request.getRequestURI(), null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {

		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred.",
				request.getRequestURI(), null);
	}

	private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, String path,
			Map<String, String> validationErrors) {

		ApiErrorResponse response = ApiErrorResponse.builder().timestamp(OffsetDateTime.now()).status(status.value())
				.error(status.getReasonPhrase()).message(message).path(path).validationErrors(validationErrors).build();

		return ResponseEntity.status(status).body(response);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex,
			HttpServletRequest request) {

		return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password.", request.getRequestURI(), null);
	}

	@ExceptionHandler({ DisabledException.class, LockedException.class })
	public ResponseEntity<ApiErrorResponse> handleUnavailableAccount(RuntimeException ex, HttpServletRequest request) {

		return buildResponse(HttpStatus.FORBIDDEN, "User account is not available.", request.getRequestURI(), null);
	}
}