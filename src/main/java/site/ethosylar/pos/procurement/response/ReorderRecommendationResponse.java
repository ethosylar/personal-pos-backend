package site.ethosylar.pos.procurement.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReorderRecommendationResponse {

	private Long ingredientId;

	private String ingredientCode;

	private String ingredientName;

	private BigDecimal currentStock;

	private BigDecimal reorderLevel;

	private BigDecimal targetStockLevel;

	private String stockStatus;

	/*
	 * Target - current stock. Always expressed in ingredient base unit.
	 */
	private BigDecimal shortageQuantity;

	private Long preferredSupplierId;

	private String preferredSupplierCode;

	private String preferredSupplierName;

	/*
	 * Example: Supplier sells beans in 1000g bags.
	 */
	private BigDecimal supplierBaseQuantity;

	/*
	 * Price for one supplier base quantity / pack.
	 */
	private BigDecimal purchasePrice;

	/*
	 * Rounded-up number of packs required.
	 */
	private BigDecimal suggestedPackCount;

	/*
	 * Pack count × supplierBaseQuantity.
	 */
	private BigDecimal suggestedPurchaseQuantity;

	private BigDecimal estimatedCost;

	private Boolean actionable;

	private String message;
}