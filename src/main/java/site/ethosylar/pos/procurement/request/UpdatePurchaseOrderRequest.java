package site.ethosylar.pos.procurement.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePurchaseOrderRequest {

	@NotNull
	private LocalDate orderDate;

	private LocalDate expectedDeliveryDate;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal taxAmount;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal deliveryFee;

	@Size(max = 1000)
	private String notes;
}