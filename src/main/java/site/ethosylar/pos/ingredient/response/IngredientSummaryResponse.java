package site.ethosylar.pos.ingredient.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientSummaryResponse {

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

	private Boolean active;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
	
	private long preferredSupplierId;
	
	private String preferredSupplierName;
	
	private BigDecimal currentUnitCost;
}