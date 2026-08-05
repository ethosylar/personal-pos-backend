package site.ethosylar.pos.sale.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.ethosylar.pos.dashboard.projection.TopProductProjection;
import site.ethosylar.pos.sale.entity.SaleItemEntity;

public interface SaleItemRepository extends JpaRepository<SaleItemEntity, Long> {

	List<SaleItemEntity> findBySale_IdOrderByIdAsc(Long saleId);

	Optional<SaleItemEntity> findByIdAndSale_Id(Long id, Long saleId);

	boolean existsBySale_IdAndProduct_Id(Long saleId, Long productId);

	@Query(value = """
			SELECT
			    si.product_id AS productId,

			    MAX(
			        si.product_code_snapshot
			    ) AS productCode,

			    MAX(
			        si.product_name_snapshot
			    ) AS productName,

			    COALESCE(
			        SUM(si.quantity),
			        0
			    ) AS quantitySold,

			    COALESCE(
			        SUM(si.line_total),
			        0
			    ) AS revenue,

			    COALESCE(
			        SUM(si.line_cost_snapshot),
			        0
			    ) AS cogs,

			    COALESCE(
			        SUM(si.gross_profit_snapshot),
			        0
			    ) AS grossProfit

			FROM dt_sale_items_si si

			JOIN dt_sales_sal sal
			    ON sal.id = si.sale_id

			JOIN st_sale_status_ss ss
			    ON ss.id = sal.status_id

			WHERE ss.code = 'COMPLETED'

			  AND sal.sold_at >= :start

			  AND sal.sold_at < :end

			GROUP BY si.product_id

			ORDER BY revenue DESC
			""", nativeQuery = true)
	List<TopProductProjection> findTopProducts(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, Pageable pageable);
}
