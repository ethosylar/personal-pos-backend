package site.ethosylar.pos.dashboard.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventorySummaryResponse {

	private Integer totalIngredients;

	private Integer healthyCount;

	private Integer lowStockCount;

	private Integer outOfStockCount;

	private Integer unconfiguredCount;

	private Integer alertCount;
	
	private BigDecimal totalInventoryValue;
}