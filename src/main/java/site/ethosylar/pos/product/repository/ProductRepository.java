package site.ethosylar.pos.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.ethosylar.pos.product.entity.ProductEntity;
import site.ethosylar.pos.product.projection.ProductSummaryProjection;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

	Optional<ProductEntity> findByCodeIgnoreCase(String code);

	boolean existsByCodeIgnoreCase(String code);

	boolean existsBySkuIgnoreCase(String sku);

	boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

	boolean existsByBarcode(String barcode);

	boolean existsByBarcodeAndIdNot(String barcode, Long id);

	@Query("""
			    SELECT
			        p.id AS id,
			        p.code AS code,
			        p.name AS name,
			        c.id AS categoryId,
			        c.name AS categoryName,
			        p.sellingPrice AS sellingPrice,
			        p.active AS active
			    FROM ProductEntity p
			    LEFT JOIN p.category c
			    WHERE
			        (:search IS NULL
			            OR LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%'))
			            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
			""")
	Page<ProductSummaryProjection> findProductSummaries(@Param("search") String search, Pageable pageable);

	@Query("""
			    SELECT p
			    FROM ProductEntity p
			    LEFT JOIN FETCH p.category
			    WHERE p.id = :id
			""")
	Optional<ProductEntity> findDetailById(@Param("id") Long id);
}