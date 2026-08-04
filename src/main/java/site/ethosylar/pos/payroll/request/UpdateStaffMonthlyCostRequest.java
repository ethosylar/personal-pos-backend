package site.ethosylar.pos.payroll.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStaffMonthlyCostRequest {

	@DecimalMin("0.0001")
	private BigDecimal unitsWorked;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal additionalAllowance;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal overtimePay;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal bonusAmount;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal deductionAmount;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal employerContribution;

	@Size(max = 1000)
	private String notes;
}