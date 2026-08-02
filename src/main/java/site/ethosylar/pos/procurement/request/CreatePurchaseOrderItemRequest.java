package site.ethosylar.pos.procurement.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePurchaseOrderItemRequest {

	@NotNull
	@Positive
	private Long ingredientId;

	@NotNull
	@DecimalMin("0.0001")
	private BigDecimal orderedQuantity;

	/*
	 * Optional override. If null, supplier master price is used.
	 */
	@DecimalMin("0.0000")
	private BigDecimal unitPrice;

	@Size(max = 1000)
	private String notes;
}