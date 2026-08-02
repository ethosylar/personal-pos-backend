package site.ethosylar.pos.sale.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSaleRequest {

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal discountAmount = BigDecimal.ZERO;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal taxAmount = BigDecimal.ZERO;

	@Size(max = 1000)
	private String notes;
}