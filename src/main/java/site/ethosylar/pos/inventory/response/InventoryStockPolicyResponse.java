package site.ethosylar.pos.inventory.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryStockPolicyResponse {

	private Long id;

	private Long ingredientId;

	private String ingredientCode;

	private String ingredientName;

	private BigDecimal reorderLevel;

	private BigDecimal targetStockLevel;

	private Boolean alertEnabled;

	private String notes;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}