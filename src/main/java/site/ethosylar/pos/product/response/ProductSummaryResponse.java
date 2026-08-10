package site.ethosylar.pos.product.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductSummaryResponse {

	private Long id;

	private String code;

	private String name;

	private Long categoryId;

	private String categoryName;

	private BigDecimal sellingPrice;

	private Boolean active;
}