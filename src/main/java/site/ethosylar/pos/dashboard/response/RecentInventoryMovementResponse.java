package site.ethosylar.pos.dashboard.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecentInventoryMovementResponse {

	private Long id;

	private Long ingredientId;

	private String ingredientCode;

	private String ingredientName;

	private String movementTypeCode;

	private String movementTypeName;

	private Short direction;

	private BigDecimal quantity;

	private BigDecimal signedQuantity;

	private BigDecimal unitCost;

	private String referenceType;

	private Long referenceId;

	private String referenceNo;

	private String reason;

	private OffsetDateTime occurredAt;

	private Long createdByUserId;

	private String createdByUsername;
}