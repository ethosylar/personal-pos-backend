package site.ethosylar.pos.supplier.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.ethosylar.pos.supplier.entity.SupplierEntity;
import site.ethosylar.pos.supplier.projection.SupplierSummaryProjection;

public interface SupplierRepository
		extends JpaRepository<SupplierEntity, Long>, JpaSpecificationExecutor<SupplierEntity> {

	Optional<SupplierEntity> findByCodeIgnoreCase(String code);

	boolean existsByCodeIgnoreCase(String code);

	Optional<SupplierEntity> findById(Long id);

	@Query("""
			    SELECT
			        s.id AS id,
			        s.code AS code,
			        s.name AS name,
			        s.contactPerson AS contactPerson,
			        s.phone AS phone,
			        s.email AS email,
			        s.active AS active

			    FROM SupplierEntity s

			    WHERE
			        (
			            :search IS NULL

			            OR LOWER(s.code)
			                LIKE LOWER(CONCAT('%', :search, '%'))

			            OR LOWER(s.name)
			                LIKE LOWER(CONCAT('%', :search, '%'))

			            OR LOWER(s.contactPerson)
			                LIKE LOWER(CONCAT('%', :search, '%'))
			        )
			""")
	Page<SupplierSummaryProjection> findSupplierSummaries(@Param("search") String search, Pageable pageable);
}