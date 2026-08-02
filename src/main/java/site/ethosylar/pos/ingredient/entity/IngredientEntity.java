package site.ethosylar.pos.ingredient.entity;

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
import site.ethosylar.pos.category.entity.IngredientCategoryEntity;
import site.ethosylar.pos.common.entity.TimestampedEntity;
import site.ethosylar.pos.unit.entity.UnitOfMeasureEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_ingredients_ing")
public class IngredientEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, length = 50, unique = true)
	private String code;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingredient_category_id", nullable = false)
	private IngredientCategoryEntity category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "base_unit_id", nullable = false)
	private UnitOfMeasureEntity baseUnit;

	@Column(name = "minimum_stock_qty", nullable = false, precision = 18, scale = 4)
	private BigDecimal minimumStockQty = BigDecimal.ZERO;

	@Column(name = "reorder_qty", nullable = false, precision = 18, scale = 4)
	private BigDecimal reorderQty = BigDecimal.ZERO;

	@Column(name = "notes", length = 1000)
	private String notes;

	@Column(name = "is_active", nullable = false)
	private Boolean active = true;
}