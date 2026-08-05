package site.ethosylar.pos.accounting.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyAccountingFilterRequest {

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