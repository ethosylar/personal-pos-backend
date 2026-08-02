package site.ethosylar.pos.procurement.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseOrderResponse {

	private Long id;

	private String poNumber;

	private Long supplierId;

	private String supplierCode;

	private String supplierName;

	private Long statusId;

	private String statusCode;

	private String statusName;

	private LocalDate orderDate;

	private LocalDate expectedDeliveryDate;

	private BigDecimal subtotal;

	private BigDecimal taxAmount;

	private BigDecimal deliveryFee;

	private BigDecimal totalAmount;

	private String notes;

	private List<PurchaseOrderItemResponse> items;

	private Long createdByUserId;

	private String createdByUsername;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}