package site.ethosylar.pos.inventory.entity;

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
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.user.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_inventory_movements_im")
public class InventoryMovementEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingredient_id", nullable = false)
	private IngredientEntity ingredient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movement_type_id", nullable = false)
	private InventoryMovementTypeEntity movementType;

	@Column(name = "quantity", nullable = false, precision = 18, scale = 4)
	private BigDecimal quantity;

	@Column(name = "unit_cost", precision = 18, scale = 6)
	private BigDecimal unitCost;

	@Column(name = "occurred_at", nullable = false)
	private OffsetDateTime occurredAt;

	@Column(name = "reference_type", length = 50)
	private String referenceType;

	@Column(name = "reference_id")
	private Long referenceId;

	@Column(name = "reference_no", length = 100)
	private String referenceNo;

	@Column(name = "reason", length = 500)
	private String reason;

	@Column(name = "notes", length = 1000)
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_user_id")
	private UserEntity createdByUser;
}