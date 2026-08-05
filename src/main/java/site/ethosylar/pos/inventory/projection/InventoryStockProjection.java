package site.ethosylar.pos.inventory.projection;

import java.math.BigDecimal;

public interface InventoryStockProjection {

	Long getIngredientId();

	String getIngredientCode();

	String getIngredientName();

	BigDecimal getCurrentStock();

	BigDecimal getReorderLevel();

	BigDecimal getTargetStockLevel();

	Boolean getAlertEnabled();
}