package site.ethosylar.pos.inventory.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.ethosylar.pos.inventory.entity.InventoryMovementEntity;

public interface InventoryMovementRepository
		extends JpaRepository<InventoryMovementEntity, Long>, JpaSpecificationExecutor<InventoryMovementEntity> {

	@Query(value = """
			SELECT COALESCE(
			    SUM(
			        im.quantity
			        * imt.direction
			    ),
			    0
			)
			FROM dt_inventory_movements_im im
			INNER JOIN lt_inventory_movement_types_imt imt
			    ON imt.id = im.movement_type_id
			WHERE im.ingredient_id = :ingredientId
			""", nativeQuery = true)
	BigDecimal calculateCurrentStock(@Param("ingredientId") Long ingredientId);

	boolean existsByIngredient_Id(Long ingredientId);
}