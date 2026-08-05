package site.ethosylar.pos.dashboard.projection;

import java.math.BigDecimal;

public interface MonthlySalesProjection {

	Integer getMonthNumber();

	Long getSalesCount();

	BigDecimal getRevenue();

	BigDecimal getCogs();
}