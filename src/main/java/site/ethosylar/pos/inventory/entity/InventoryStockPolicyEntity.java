package site.ethosylar.pos.inventory.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "dt_inventory_stock_policies_isp")
public class InventoryStockPolicyEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingredient_id", nullable = false, unique = true)
	private IngredientEntity ingredient;

	@Column(name = "reorder_level", nullable = false, precision = 18, scale = 4)
	private BigDecimal reorderLevel = BigDecimal.ZERO;

	@Column(name = "target_stock_level", precision = 18, scale = 4)
	private BigDecimal targetStockLevel;

	@Column(name = "alert_enabled", nullable = false)
	private Boolean alertEnabled = true;

	@Column(name = "notes", length = 1000)
	private String notes;
}