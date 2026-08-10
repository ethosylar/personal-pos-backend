package site.ethosylar.pos.procurement.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public interface PurchaseOrderSummaryProjection {

	Long getId();

	String getPoNumber();

	String getStatusCode();

	String getStatusName();

	Long getSupplierId();

	String getSupplierCode();

	String getSupplierName();

	LocalDate getOrderDate();

	LocalDate getExpectedDeliveryDate();

	Long getItemCount();

	BigDecimal getSubtotal();

	BigDecimal getTaxAmount();

	BigDecimal getDeliveryFee();

	BigDecimal getGrandTotal();

	OffsetDateTime getCreatedAt();
}