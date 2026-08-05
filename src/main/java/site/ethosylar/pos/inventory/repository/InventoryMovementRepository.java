package site.ethosylar.pos.inventory.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.ethosylar.pos.inventory.entity.InventoryMovementEntity;
import site.ethosylar.pos.inventory.projection.InventoryStockProjection;
import site.ethosylar.pos.inventory.projection.RecentInventoryMovementProjection;

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

	boolean existsByReferenceTypeIgnoreCaseAndReferenceIdAndIngredient_IdAndMovementType_Id(String referenceType,
			Long referenceId, Long ingredientId, Long movementTypeId);

	List<InventoryMovementEntity> findByReferenceTypeIgnoreCaseAndReferenceIdAndMovementType_CodeIgnoreCaseOrderByIdAsc(
			String referenceType, Long referenceId, String movementTypeCode);

	@Query(value = """
			SELECT
			    ing.id AS ingredientId,
			    ing.code AS ingredientCode,
			    ing.name AS ingredientName,

			    COALESCE(
			        SUM(
			            im.quantity
			            * imt.direction
			        ),
			        0
			    ) AS currentStock,

			    COALESCE(
			        isp.reorder_level,
			        0
			    ) AS reorderLevel,

			    isp.target_stock_level
			        AS targetStockLevel,

			    COALESCE(
			        isp.alert_enabled,
			        FALSE
			    ) AS alertEnabled

			FROM dt_ingredients_ing ing

			LEFT JOIN dt_inventory_movements_im im
			    ON im.ingredient_id = ing.id

			LEFT JOIN lt_inventory_movement_types_imt imt
			    ON imt.id = im.movement_type_id

			LEFT JOIN dt_inventory_stock_policies_isp isp
			    ON isp.ingredient_id = ing.id

			GROUP BY
			    ing.id,
			    ing.code,
			    ing.name,
			    isp.reorder_level,
			    isp.target_stock_level,
			    isp.alert_enabled

			ORDER BY ing.name
			""", nativeQuery = true)
	List<InventoryStockProjection> findInventoryStockIntelligence();

	@Query(value = """
			SELECT
			    im.id AS id,

			    ing.id AS ingredientId,
			    ing.code AS ingredientCode,
			    ing.name AS ingredientName,

			    imt.code AS movementTypeCode,
			    imt.name AS movementTypeName,
			    imt.direction AS direction,

			    im.quantity AS quantity,

			    im.unit_cost AS unitCost,

			    im.reference_type AS referenceType,
			    im.reference_id AS referenceId,
			    im.reference_no AS referenceNo,

			    im.reason AS reason,

			    im.occurred_at AS occurredAt,

			    usr.id AS createdByUserId,
			    usr.username AS createdByUsername

			FROM dt_inventory_movements_im im

			JOIN dt_ingredients_ing ing
			    ON ing.id = im.ingredient_id

			JOIN lt_inventory_movement_types_imt imt
			    ON imt.id = im.movement_type_id

			LEFT JOIN dt_users_usr usr
			    ON usr.id = im.created_by_user_id

			ORDER BY
			    im.occurred_at DESC,
			    im.id DESC
			""", nativeQuery = true)
	List<RecentInventoryMovementProjection> findRecentMovements(Pageable pageable);
}