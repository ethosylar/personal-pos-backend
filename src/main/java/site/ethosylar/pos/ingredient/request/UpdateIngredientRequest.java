package site.ethosylar.pos.ingredient.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateIngredientRequest {

	@NotBlank
	@Size(max = 150)
	private String name;

	@NotNull
	@Positive
	private Long ingredientCategoryId;

	@NotNull
	@DecimalMin(value = "0.0000")
	private BigDecimal minimumStockQty;

	@NotNull
	@DecimalMin(value = "0.0000")
	private BigDecimal reorderQty;

	@Size(max = 1000)
	private String notes;
}