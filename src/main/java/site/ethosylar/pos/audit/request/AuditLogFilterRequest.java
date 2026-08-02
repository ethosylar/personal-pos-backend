package site.ethosylar.pos.audit.request;

import java.time.OffsetDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditLogFilterRequest {

	@Positive
	private Long userId;

	private String actionCode;

	private String moduleCode;

	private String entityType;

	@Positive
	private Long entityId;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private OffsetDateTime fromDate;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private OffsetDateTime toDate;

	@Min(1)
	private Integer page = 1;

	@Min(1)
	@Max(100)
	private Integer perPage = 20;
}