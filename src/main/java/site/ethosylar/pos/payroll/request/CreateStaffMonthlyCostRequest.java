package site.ethosylar.pos.payroll.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStaffMonthlyCostRequest {

	@NotNull
	@Positive
	private Long staffId;

	@NotNull
	@Min(2000)
	private Integer periodYear;

	@NotNull
	@Min(1)
	@Max(12)
	private Integer periodMonth;

	/*
	 * Required for DAILY / HOURLY. Normally null for MONTHLY.
	 */
	@DecimalMin("0.0001")
	private BigDecimal unitsWorked;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal additionalAllowance = BigDecimal.ZERO;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal overtimePay = BigDecimal.ZERO;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal bonusAmount = BigDecimal.ZERO;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal deductionAmount = BigDecimal.ZERO;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal employerContribution = BigDecimal.ZERO;

	private String notes;
}