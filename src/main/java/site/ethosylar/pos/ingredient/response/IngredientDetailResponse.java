package site.ethosylar.pos.ingredient.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientDetailResponse {

	private Long id;

	private String code;

	private String name;

	private Long categoryId;

	private String categoryCode;

	private String categoryName;

	private Long baseUnitId;

	private String baseUnitCode;

	private String baseUnitName;

	private String baseUnitSymbol;

	private BigDecimal minimumStockQty;

	private BigDecimal reorderQty;

	private BigDecimal preferredCostPerBaseUnit;

	private String notes;

	private Boolean active;

	private List<IngredientSupplierResponse> suppliers;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}