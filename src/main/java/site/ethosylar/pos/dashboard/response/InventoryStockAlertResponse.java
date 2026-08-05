package site.ethosylar.pos.dashboard.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryStockAlertResponse {

	private Long ingredientId;

	private String ingredientCode;

	private String ingredientName;

	private BigDecimal currentStock;

	private BigDecimal reorderLevel;

	private BigDecimal targetStockLevel;

	private BigDecimal unitCost;

	private BigDecimal inventoryValue;

	/*
	 * OUT_OF_STOCK LOW_STOCK HEALTHY UNCONFIGURED
	 */
	private String stockStatus;

	private BigDecimal suggestedReorderQuantity;

	private Boolean alertEnabled;
}