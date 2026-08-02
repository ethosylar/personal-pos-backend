package site.ethosylar.pos.sale.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaleItemResponse {

	private Long id;

	private Long productId;

	private String productCode;

	private String productName;

	private Long recipeId;

	private Integer recipeVersionNo;

	private BigDecimal quantity;

	private BigDecimal unitPrice;

	private BigDecimal lineSubtotal;

	private BigDecimal discountAmount;

	private BigDecimal lineTotal;

	private BigDecimal unitCostSnapshot;

	private BigDecimal lineCostSnapshot;

	private BigDecimal grossProfitSnapshot;

	private String notes;
}