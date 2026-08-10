package site.ethosylar.pos.procurement.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseOrderSummaryResponse {

	private Long id;

	private String poNumber;

	private String statusCode;

	private String statusName;

	private Long supplierId;

	private String supplierCode;

	private String supplierName;

	private LocalDate orderDate;

	private LocalDate expectedDeliveryDate;

	private Integer itemCount;

	private BigDecimal subtotal;

	private BigDecimal taxAmount;

	private BigDecimal deliveryFee;

	private BigDecimal grandTotal;

	private OffsetDateTime createdAt;
}