package site.ethosylar.pos.inventory.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryReturnRequest {

	@NotNull
	@Positive
	private Long ingredientId;

	@NotBlank
	private String direction;

	@NotNull
	@DecimalMin("0.0001")
	private BigDecimal quantity;

	@DecimalMin("0.0000")
	private BigDecimal unitCost;

	private OffsetDateTime occurredAt;

	@Size(max = 500)
	private String reason;

	@Size(max = 1000)
	private String notes;
}