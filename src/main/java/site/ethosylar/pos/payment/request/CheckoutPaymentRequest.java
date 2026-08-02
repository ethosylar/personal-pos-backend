package site.ethosylar.pos.payment.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutPaymentRequest {

	@NotNull
	@Positive
	private Long paymentMethodId;

	/*
	 * Amount applied to the sale.
	 *
	 * Example: Sale RM13 Customer gives RM20 cash
	 *
	 * amount = 13 tenderedAmount = 20
	 */
	@NotNull
	@DecimalMin("0.0001")
	private BigDecimal amount;

	@DecimalMin("0.0000")
	private BigDecimal tenderedAmount;

	@Size(max = 150)
	private String referenceNo;

	@Size(max = 1000)
	private String notes;
}