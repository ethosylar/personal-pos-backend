package site.ethosylar.pos.procurement.response;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoodsReceiptResponse {

	private Long id;

	private String receiptNumber;

	private Long purchaseOrderId;

	private String poNumber;

	private OffsetDateTime receivedAt;

	private String supplierDeliveryNo;

	private String notes;

	private Long receivedByUserId;

	private String receivedByUsername;

	private List<GoodsReceiptItemResponse> items;

	private OffsetDateTime createdAt;
}