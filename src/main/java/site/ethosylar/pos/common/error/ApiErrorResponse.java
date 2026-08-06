package site.ethosylar.pos.common.error;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiErrorResponse {

	private OffsetDateTime timestamp;

	private Integer status;

	private String error;

	private String code;

	private String message;

	private String path;

	private List<ApiFieldError> fieldErrors;

	/*
	 * Useful later when correlating logs.
	 */
	private String traceId;
}