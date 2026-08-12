package site.ethosylar.pos.sale.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SaleCompletedEventPayload(

		Long saleId,

		String saleNumber,

		OffsetDateTime soldAt,

		BigDecimal subtotal,

		BigDecimal totalAmount,

		BigDecimal totalCost,

		BigDecimal grossProfit

) {
}