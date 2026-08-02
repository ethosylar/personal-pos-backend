package site.ethosylar.pos.procurement.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGoodsReceiptItemRequest {

	@NotNull
	@Positive
	private Long purchaseOrderItemId;

	@NotNull
	@DecimalMin("0.0001")
	private BigDecimal receivedQuantity;

	@Size(max = 1000)
	private String notes;
}