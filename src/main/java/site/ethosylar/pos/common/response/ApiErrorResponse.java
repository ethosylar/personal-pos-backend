package site.ethosylar.pos.common.response;

import java.time.OffsetDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiErrorResponse {

    private OffsetDateTime timestamp;

    private Integer status;

    private String error;

    private String message;

    private String path;

    private Map<String, String> validationErrors;
}