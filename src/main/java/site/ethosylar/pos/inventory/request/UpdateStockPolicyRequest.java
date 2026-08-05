package site.ethosylar.pos.inventory.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStockPolicyRequest {

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal reorderLevel;

	@DecimalMin("0.0000")
	private BigDecimal targetStockLevel;

	@NotNull
	private Boolean alertEnabled;

	@Size(max = 1000)
	private String notes;
}