package site.ethosylar.pos.procurement.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePurchaseOrderRequest {

	@NotNull
	@Positive
	private Long supplierId;

	@NotNull
	private LocalDate orderDate;

	private LocalDate expectedDeliveryDate;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal taxAmount = BigDecimal.ZERO;

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal deliveryFee = BigDecimal.ZERO;

	@Size(max = 1000)
	private String notes;

	@Valid
	private List<CreatePurchaseOrderItemRequest> items = new ArrayList<>();
}