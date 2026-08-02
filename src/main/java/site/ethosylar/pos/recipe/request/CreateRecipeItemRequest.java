package site.ethosylar.pos.recipe.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRecipeItemRequest {

	@NotNull
	@Positive
	private Long ingredientId;

	@NotNull
	@DecimalMin(value = "0.0001", message = "Quantity must be greater than zero.")
	private BigDecimal quantity;

	@NotNull
	@DecimalMin("0.0000")
	@DecimalMax("100.0000")
	private BigDecimal wastePercent = BigDecimal.ZERO;

	@Min(0)
	private Integer displayOrder = 0;

	@Size(max = 1000)
	private String notes;
}