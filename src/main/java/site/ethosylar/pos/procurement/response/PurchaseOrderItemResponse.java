package site.ethosylar.pos.procurement.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseOrderItemResponse {

	private Long id;

	private Long ingredientId;

	private String ingredientCode;

	private String ingredientName;

	private Long purchaseUnitId;

	private String purchaseUnitCode;

	private String purchaseUnitSymbol;

	private BigDecimal orderedQuantity;

	private BigDecimal receivedQuantity;

	private BigDecimal remainingQuantity;

	private BigDecimal baseQuantityPerUnit;

	private BigDecimal unitPrice;

	private BigDecimal lineSubtotal;

	private String supplierItemCode;

	private String notes;
}