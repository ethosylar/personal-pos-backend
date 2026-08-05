package site.ethosylar.pos.accounting.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateMonthlyAccountingRequest {

	@NotNull
	@Min(2000)
	private Integer periodYear;

	@NotNull
	@Min(1)
	@Max(12)
	private Integer periodMonth;

	@Size(max = 1000)
	private String notes;
}