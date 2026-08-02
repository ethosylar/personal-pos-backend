package site.ethosylar.pos.product.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeProductPriceRequest {

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal sellingPrice;
}