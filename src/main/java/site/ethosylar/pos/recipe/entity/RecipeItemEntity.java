package site.ethosylar.pos.recipe.entity;

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
import site.ethosylar.pos.ingredient.entity.IngredientEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_recipe_items_ri")
public class RecipeItemEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipe_id", nullable = false)
	private RecipeEntity recipe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingredient_id", nullable = false)
	private IngredientEntity ingredient;

	@Column(name = "quantity", nullable = false, precision = 18, scale = 4)
	private BigDecimal quantity;

	@Column(name = "waste_percent", nullable = false, precision = 8, scale = 4)
	private BigDecimal wastePercent = BigDecimal.ZERO;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder = 0;

	@Column(name = "unit_cost_snapshot", precision = 18, scale = 6)
	private BigDecimal unitCostSnapshot;

	@Column(name = "line_cost_snapshot", precision = 18, scale = 6)
	private BigDecimal lineCostSnapshot;

	@Column(name = "notes", length = 1000)
	private String notes;
}