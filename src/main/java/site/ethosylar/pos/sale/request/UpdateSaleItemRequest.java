package site.ethosylar.pos.sale.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSaleItemRequest {

	@NotNull
	@DecimalMin("0.0001")
	private BigDecimal quantity;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal discountAmount;

	@Size(max = 1000)
	private String notes;
}