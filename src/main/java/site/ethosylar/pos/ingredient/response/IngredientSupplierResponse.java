package site.ethosylar.pos.ingredient.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientSupplierResponse {

	private Long id;

	private Long supplierId;

	private String supplierCode;

	private String supplierName;

	private String supplierItemCode;

	private Long purchaseUnitId;

	private String purchaseUnitCode;

	private String purchaseUnitName;

	private String purchaseUnitSymbol;

	private BigDecimal baseQuantity;

	private BigDecimal purchasePrice;

	private BigDecimal costPerBaseUnit;

	private Boolean preferred;

	private Boolean active;

	private String notes;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}