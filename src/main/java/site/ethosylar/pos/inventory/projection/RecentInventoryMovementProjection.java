package site.ethosylar.pos.inventory.projection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface RecentInventoryMovementProjection {

	Long getId();

	Long getIngredientId();

	String getIngredientCode();

	String getIngredientName();

	String getMovementTypeCode();

	String getMovementTypeName();

	Short getDirection();

	BigDecimal getQuantity();

	BigDecimal getUnitCost();

	String getReferenceType();

	Long getReferenceId();

	String getReferenceNo();

	String getReason();

	OffsetDateTime getOccurredAt();

	Long getCreatedByUserId();

	String getCreatedByUsername();
}