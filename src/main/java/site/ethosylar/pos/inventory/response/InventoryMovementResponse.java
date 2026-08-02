package site.ethosylar.pos.inventory.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryMovementResponse {

	private Long id;

	private Long ingredientId;

	private String ingredientCode;

	private String ingredientName;

	private String baseUnitCode;

	private String baseUnitSymbol;

	private Long movementTypeId;

	private String movementTypeCode;

	private String movementTypeName;

	private Integer direction;

	private BigDecimal quantity;

	/*
	 * quantity × direction
	 *
	 * Example: WASTE 50g → -50g
	 */
	private BigDecimal signedQuantity;

	private BigDecimal unitCost;

	private BigDecimal movementValue;

	private OffsetDateTime occurredAt;

	private String referenceType;

	private Long referenceId;

	private String referenceNo;

	private String reason;

	private String notes;

	private Long createdByUserId;

	private String createdByUsername;

	private OffsetDateTime createdAt;
}