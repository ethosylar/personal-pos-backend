package site.ethosylar.pos.sale.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.ethosylar.pos.common.entity.TimestampedEntity;
import site.ethosylar.pos.product.entity.ProductEntity;
import site.ethosylar.pos.recipe.entity.RecipeEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_sale_items_si")
public class SaleItemEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sale_id", nullable = false)
	private SaleEntity sale;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipe_id")
	private RecipeEntity recipe;

	@Column(name = "product_code_snapshot", nullable = false, length = 50)
	private String productCodeSnapshot;

	@Column(name = "product_name_snapshot", nullable = false, length = 150)
	private String productNameSnapshot;

	@Column(name = "recipe_version_no")
	private Integer recipeVersionNo;

	@Column(name = "quantity", nullable = false, precision = 18, scale = 4)
	private BigDecimal quantity;

	@Column(name = "unit_price", nullable = false, precision = 18, scale = 4)
	private BigDecimal unitPrice;

	@Column(name = "line_subtotal", nullable = false, precision = 18, scale = 4)
	private BigDecimal lineSubtotal;

	@Column(name = "discount_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal discountAmount = BigDecimal.ZERO;

	@Column(name = "line_total", nullable = false, precision = 18, scale = 4)
	private BigDecimal lineTotal;

	@Column(name = "unit_cost_snapshot", precision = 18, scale = 6)
	private BigDecimal unitCostSnapshot;

	@Column(name = "line_cost_snapshot", precision = 18, scale = 6)
	private BigDecimal lineCostSnapshot;

	@Column(name = "gross_profit_snapshot", precision = 18, scale = 6)
	private BigDecimal grossProfitSnapshot;

	@Column(name = "notes", length = 1000)
	private String notes;
}