package site.ethosylar.pos.procurement.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoodsReceiptItemResponse {

    private Long id;

    private Long purchaseOrderItemId;

    private Long ingredientId;

    private String ingredientCode;

    private String ingredientName;

    private BigDecimal receivedQuantity;

    private String purchaseUnitCode;

    private BigDecimal baseQuantityPerUnit;

    private BigDecimal receivedBaseQuantity;

    private String baseUnitCode;

    private String baseUnitSymbol;

    private BigDecimal unitPrice;

    private BigDecimal baseUnitCost;

    private String notes;
}