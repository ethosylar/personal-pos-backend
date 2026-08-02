package site.ethosylar.pos.product.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

	private Long id;

	private String code;

	private String name;

	private Long categoryId;

	private String categoryCode;

	private String categoryName;

	private String sku;

	private String barcode;

	private String description;

	private BigDecimal sellingPrice;

	/*
	 * Will remain null until Recipe & Costing exists.
	 */
	private BigDecimal calculatedCost;

	private Integer displayOrder;

	private Boolean available;

	private Boolean active;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}