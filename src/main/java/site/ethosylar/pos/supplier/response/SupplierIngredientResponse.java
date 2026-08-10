package site.ethosylar.pos.supplier.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupplierIngredientResponse {

	private Long ingredientId;

	private String ingredientCode;

	private String ingredientName;

	private BigDecimal purchasePrice;

	private BigDecimal baseQuantity;

	private Boolean preferred;

	private Boolean active;
}