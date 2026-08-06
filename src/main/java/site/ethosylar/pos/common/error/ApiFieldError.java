package site.ethosylar.pos.common.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiFieldError {

	private String field;

	private String code;

	private String message;
}