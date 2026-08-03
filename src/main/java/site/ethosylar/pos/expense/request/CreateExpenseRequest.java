package site.ethosylar.pos.expense.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateExpenseRequest {

	@NotNull
	@Positive
	private Long categoryId;

	/*
	 * Optional.
	 *
	 * Useful when the expense payee is also one of your configured suppliers.
	 */
	@Positive
	private Long supplierId;

	@NotNull
	private LocalDate expenseDate;

	@NotBlank
	@Size(max = 500)
	private String description;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal amount;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal taxAmount = BigDecimal.ZERO;

	@Size(max = 150)
	private String payeeName;

	@Size(max = 150)
	private String referenceNo;

	@Size(max = 1000)
	private String notes;
}