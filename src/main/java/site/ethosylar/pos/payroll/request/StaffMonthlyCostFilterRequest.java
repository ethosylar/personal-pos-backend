package site.ethosylar.pos.payroll.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffMonthlyCostFilterRequest {

	private String search;

	@Positive
	private Long staffId;

	private String statusCode;

	@Min(2000)
	private Integer periodYear;

	@Min(1)
	@Max(12)
	private Integer periodMonth;

	@Min(1)
	private Integer page = 1;

	@Min(1)
	@Max(100)
	private Integer perPage = 20;
}