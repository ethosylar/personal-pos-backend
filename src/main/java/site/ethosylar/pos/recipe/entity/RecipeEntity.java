package site.ethosylar.pos.recipe.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_recipes_rcp")
public class RecipeEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity product;

	@Column(name = "version_no", nullable = false)
	private Integer versionNo;

	@Column(name = "yield_quantity", nullable = false, precision = 18, scale = 4)
	private BigDecimal yieldQuantity = BigDecimal.ONE;

	@Column(name = "effective_from")
	private OffsetDateTime effectiveFrom;

	@Column(name = "effective_to")
	private OffsetDateTime effectiveTo;

	@Column(name = "is_current", nullable = false)
	private Boolean current = false;

	@Column(name = "activated_cost_snapshot", precision = 18, scale = 6)
	private BigDecimal activatedCostSnapshot;

	@Column(name = "activated_at")
	private OffsetDateTime activatedAt;

	@Column(name = "notes", length = 1000)
	private String notes;
}