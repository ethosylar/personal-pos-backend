package site.ethosylar.pos.sale.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSaleRequest {

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal discountAmount;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal taxAmount;

	@Size(max = 1000)
	private String notes;
}