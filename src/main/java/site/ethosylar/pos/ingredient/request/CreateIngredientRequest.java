package site.ethosylar.pos.ingredient.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateIngredientRequest {

	@NotBlank
	@Size(max = 50)
	@Pattern(regexp = "^[A-Z0-9_-]+$", message = "Code may only contain uppercase letters, numbers, underscores and hyphens.")
	private String code;

	@NotBlank
	@Size(max = 150)
	private String name;

	@NotNull
	@Positive
	private Long ingredientCategoryId;

	@NotNull
	@Positive
	private Long baseUnitId;

	@NotNull
	@DecimalMin(value = "0.0000")
	private BigDecimal minimumStockQty = BigDecimal.ZERO;

	@NotNull
	@DecimalMin(value = "0.0000")
	private BigDecimal reorderQty = BigDecimal.ZERO;

	@Size(max = 1000)
	private String notes;
}