package site.ethosylar.pos.product.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailResponse {

	private Long id;

	private String code;

	private String name;

	private Long categoryId;

	private String categoryCode;

	private String categoryName;

	private BigDecimal sellingPrice;

	private String description;

	private Boolean active;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}