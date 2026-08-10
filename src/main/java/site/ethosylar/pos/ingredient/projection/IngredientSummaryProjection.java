package site.ethosylar.pos.ingredient.projection;

import java.math.BigDecimal;

public interface IngredientSummaryProjection {

	Long getId();

	String getCode();

	String getName();

	Long getBaseUnitId();

	String getBaseUnitCode();

	String getBaseUnitName();

	Long getPreferredSupplierId();

	String getPreferredSupplierName();

	BigDecimal getPurchasePrice();

	BigDecimal getSupplierBaseQuantity();

	Boolean getActive();
}