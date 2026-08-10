package site.ethosylar.pos.sale.projection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface SaleSummaryProjection {

	Long getId();

	String getSaleNumber();

	String getStatusCode();

	String getStatusName();

	OffsetDateTime getSoldAt();

	Long getItemCount();

	BigDecimal getSubtotal();

	BigDecimal getDiscountAmount();

	BigDecimal getTaxAmount();

	BigDecimal getTotalAmount();

	BigDecimal getTotalCost();

	BigDecimal getGrossProfit();
}