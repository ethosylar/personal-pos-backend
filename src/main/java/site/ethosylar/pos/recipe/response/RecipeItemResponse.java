package site.ethosylar.pos.recipe.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeItemResponse {

	private Long id;

	private Long ingredientId;

	private String ingredientCode;

	private String ingredientName;

	private Long baseUnitId;

	private String baseUnitCode;

	private String baseUnitSymbol;

	private BigDecimal quantity;

	private BigDecimal wastePercent;

	private BigDecimal effectiveQuantity;

	private BigDecimal currentUnitCost;

	private BigDecimal currentLineCost;

	private BigDecimal activatedUnitCostSnapshot;

	private BigDecimal activatedLineCostSnapshot;

	private Boolean costAvailable;

	private String costingWarning;

	private Integer displayOrder;

	private String notes;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}