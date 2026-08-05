package site.ethosylar.pos.dashboard.projection;

import java.math.BigDecimal;

public interface TopProductProjection {

	Long getProductId();

	String getProductCode();

	String getProductName();

	BigDecimal getQuantitySold();

	BigDecimal getRevenue();

	BigDecimal getCogs();

	BigDecimal getGrossProfit();
}