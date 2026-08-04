package site.ethosylar.pos.staff.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSalaryRateRequest {

	@NotNull
	@Positive
	private Long payTypeId;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal baseRate;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal fixedAllowance = BigDecimal.ZERO;

	@NotNull
	private LocalDate effectiveFrom;

	@Size(max = 1000)
	private String notes;
}