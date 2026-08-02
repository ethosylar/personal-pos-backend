package site.ethosylar.pos.inventory.request;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryMovementFilterRequest {

	private String search;

	@Positive
	private Long ingredientId;

	private String movementTypeCode;

	private String referenceType;

	private OffsetDateTime fromDate;

	private OffsetDateTime toDate;

	@Min(1)
	private Integer page = 1;

	@Min(1)
	@Max(100)
	private Integer perPage = 20;
}