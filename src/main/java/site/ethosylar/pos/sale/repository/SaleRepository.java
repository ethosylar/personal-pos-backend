package site.ethosylar.pos.sale.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import site.ethosylar.pos.dashboard.projection.MonthlySalesProjection;
import site.ethosylar.pos.sale.entity.SaleEntity;

public interface SaleRepository extends JpaRepository<SaleEntity, Long>, JpaSpecificationExecutor<SaleEntity> {

	Optional<SaleEntity> findBySaleNumberIgnoreCase(String saleNumber);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT s
			FROM SaleEntity s
			WHERE s.id = :id
			""")
	Optional<SaleEntity> findByIdForUpdate(@Param("id") Long id);

	long countByStatus_CodeIgnoreCaseAndSoldAtGreaterThanEqualAndSoldAtLessThan(String statusCode, OffsetDateTime start,
			OffsetDateTime end);

	@Query("""
			SELECT
			    COALESCE(SUM(s.subtotal), 0)
			    -
			    COALESCE(SUM(s.discountAmount), 0)
			FROM SaleEntity s
			WHERE UPPER(s.status.code) = UPPER(:statusCode)
			  AND s.soldAt >= :start
			  AND s.soldAt < :end
			""")
	BigDecimal calculateNetSalesRevenue(@Param("statusCode") String statusCode, @Param("start") OffsetDateTime start,
			@Param("end") OffsetDateTime end);

	@Query("""
			SELECT COALESCE(SUM(s.taxAmount), 0)
			FROM SaleEntity s
			WHERE UPPER(s.status.code) = UPPER(:statusCode)
			  AND s.soldAt >= :start
			  AND s.soldAt < :end
			""")
	BigDecimal calculateTaxCollected(@Param("statusCode") String statusCode, @Param("start") OffsetDateTime start,
			@Param("end") OffsetDateTime end);

	@Query("""
			SELECT COALESCE(SUM(s.totalCostSnapshot), 0)
			FROM SaleEntity s
			WHERE UPPER(s.status.code) = UPPER(:statusCode)
			  AND s.soldAt >= :start
			  AND s.soldAt < :end
			""")
	BigDecimal calculateCogs(@Param("statusCode") String statusCode, @Param("start") OffsetDateTime start,
			@Param("end") OffsetDateTime end);

	@Query(value = """
			SELECT
			    EXTRACT(
			        MONTH FROM (
			            sal.sold_at
			            AT TIME ZONE 'Asia/Kuala_Lumpur'
			        )
			    )::INTEGER AS monthNumber,

			    COUNT(*) AS salesCount,

			    COALESCE(
			        SUM(
			            sal.subtotal
			            - sal.discount_amount
			        ),
			        0
			    ) AS revenue,

			    COALESCE(
			        SUM(
			            sal.total_cost_snapshot
			        ),
			        0
			    ) AS cogs

			FROM dt_sales_sal sal

			JOIN st_sale_status_ss ss
			    ON ss.id = sal.status_id

			WHERE ss.code = 'COMPLETED'

			  AND sal.sold_at >= :start

			  AND sal.sold_at < :end

			GROUP BY
			    EXTRACT(
			        MONTH FROM (
			            sal.sold_at
			            AT TIME ZONE 'Asia/Kuala_Lumpur'
			        )
			    )

			ORDER BY monthNumber
			""", nativeQuery = true)
	List<MonthlySalesProjection> findMonthlySalesTrend(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

}
