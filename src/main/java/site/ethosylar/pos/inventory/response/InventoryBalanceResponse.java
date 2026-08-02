package site.ethosylar.pos.inventory.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import site.ethosylar.pos.inventory.constant.InventoryStockStatus;

@Getter
@Builder
public class InventoryBalanceResponse {

	private Long ingredientId;

	private String ingredientCode;

	private String ingredientName;

	private Long categoryId;

	private String categoryCode;

	private String categoryName;

	private Long baseUnitId;

	private String baseUnitCode;

	private String baseUnitSymbol;

	private BigDecimal currentStock;

	private BigDecimal minimumStockQty;

	private BigDecimal reorderQty;

	private InventoryStockStatus stockStatus;

	private Boolean reorderSuggested;

	/*
	 * Current preferred supplier cost. Not historical valuation.
	 */
	private BigDecimal currentEstimatedUnitCost;

	private BigDecimal estimatedStockValue;

	private Boolean active;
}