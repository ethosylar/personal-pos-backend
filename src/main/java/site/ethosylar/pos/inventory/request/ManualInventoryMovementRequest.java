package site.ethosylar.pos.inventory.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManualInventoryMovementRequest {

	@NotNull
	@Positive
	private Long ingredientId;

	@NotNull
	@DecimalMin(value = "0.0001", message = "Quantity must be greater than zero.")
	private BigDecimal quantity;

	/*
	 * Cost per BASE UNIT.
	 *
	 * Optional because quantity tracking can still work when exact cost is unknown.
	 */
	@DecimalMin("0.0000")
	private BigDecimal unitCost;

	/*
	 * If omitted, server uses current timestamp.
	 */
	private OffsetDateTime occurredAt;

	@Size(max = 500)
	private String reason;

	@Size(max = 1000)
	private String notes;
}