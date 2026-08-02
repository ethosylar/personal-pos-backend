package site.ethosylar.pos.recipe.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRecipeRequest {

	@NotNull
	@DecimalMin(value = "0.0001", message = "Yield quantity must be greater than zero.")
	private BigDecimal yieldQuantity = BigDecimal.ONE;

	@Size(max = 1000)
	private String notes;
}