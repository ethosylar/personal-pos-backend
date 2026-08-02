package site.ethosylar.pos.ingredient.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateIngredientSupplierRequest {

	@Size(max = 100)
	private String supplierItemCode;

	@NotNull
	@Positive
	private Long purchaseUnitId;

	@NotNull
	@DecimalMin(value = "0.0001", message = "Base quantity must be greater than zero.")
	private BigDecimal baseQuantity;

	@NotNull
	@DecimalMin(value = "0.0000")
	private BigDecimal purchasePrice;

	private Boolean preferred = false;

	@Size(max = 1000)
	private String notes;
}