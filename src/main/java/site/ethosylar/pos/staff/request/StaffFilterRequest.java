package site.ethosylar.pos.staff.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffFilterRequest {

	private String search;

	private String statusCode;

	@Positive
	private Long userId;

	private LocalDate hiredFrom;

	private LocalDate hiredTo;

	@Min(1)
	private Integer page = 1;

	@Min(1)
	@Max(100)
	private Integer perPage = 20;
}