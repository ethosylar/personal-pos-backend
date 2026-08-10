package site.ethosylar.pos.ingredient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.projection.IngredientLookupProjection;
import site.ethosylar.pos.ingredient.projection.IngredientSummaryProjection;

public interface IngredientRepository
		extends JpaRepository<IngredientEntity, Long>, JpaSpecificationExecutor<IngredientEntity> {

	Optional<IngredientEntity> findByCodeIgnoreCase(String code);

	boolean existsByCodeIgnoreCase(String code);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT i
			FROM IngredientEntity i
			WHERE i.id = :id
			""")
	Optional<IngredientEntity> findByIdForUpdate(@Param("id") Long id);

	@EntityGraph(attributePaths = { "baseUnit" })
	Optional<IngredientEntity> findDetailById(Long id);

	@Query("""
			    SELECT
			        i.id AS id,
			        i.code AS code,
			        i.name AS name,

			        u.id AS baseUnitId,
			        u.code AS baseUnitCode,
			        u.name AS baseUnitName,

			        s.id AS preferredSupplierId,
			        s.name AS preferredSupplierName,

			        isp.purchasePrice AS purchasePrice,
			        isp.baseQuantity AS supplierBaseQuantity,

			        i.active AS active

			    FROM IngredientEntity i

			    LEFT JOIN i.baseUnit u

			    LEFT JOIN IngredientSupplierEntity isp
			        ON isp.ingredient.id = i.id
			        AND isp.preferred = true
			        AND isp.active = true

			    LEFT JOIN isp.supplier s

			    WHERE
			        (
			            :search IS NULL
			            OR LOWER(i.code)
			                LIKE LOWER(CONCAT('%', :search, '%'))
			            OR LOWER(i.name)
			                LIKE LOWER(CONCAT('%', :search, '%'))
			        )
			""")
	Page<IngredientSummaryProjection> findIngredientSummaries(@Param("search") String search, Pageable pageable);

	@Query("""
			    SELECT
			        i.id AS id,
			        i.code AS code,
			        i.name AS name
			    FROM IngredientEntity i
			    WHERE i.active = true
			    ORDER BY i.name
			""")
	List<IngredientLookupProjection> findActiveIngredientLookups();
}