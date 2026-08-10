package site.ethosylar.pos.procurement.repository;

import java.time.LocalDate;
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
import site.ethosylar.pos.procurement.entity.PurchaseOrderEntity;
import site.ethosylar.pos.procurement.projection.PurchaseOrderSummaryProjection;

public interface PurchaseOrderRepository
		extends JpaRepository<PurchaseOrderEntity, Long>, JpaSpecificationExecutor<PurchaseOrderEntity> {

	Optional<PurchaseOrderEntity> findByPoNumberIgnoreCase(String poNumber);

	boolean existsByPoNumberIgnoreCase(String poNumber);

	@EntityGraph(attributePaths = { "supplier", "status" })
	@Query("""
			    SELECT po
			    FROM PurchaseOrderEntity po
			    WHERE po.id = :id
			""")
	Optional<PurchaseOrderEntity> findDetailHeaderById(@Param("id") Long id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT po
			FROM PurchaseOrderEntity po
			WHERE po.id = :id
			""")
	Optional<PurchaseOrderEntity> findByIdForUpdate(@Param("id") Long id);

	@Query(value = """
			SELECT
			    po.id AS id,
			    po.po_number AS poNumber,

			    ps.code AS statusCode,
			    ps.name AS statusName,

			    sup.id AS supplierId,
			    sup.code AS supplierCode,
			    sup.name AS supplierName,

			    po.order_date AS orderDate,
			    po.expected_delivery_date AS expectedDeliveryDate,

			    (
			        SELECT COUNT(*)
			        FROM dt_purchase_order_items_poi item
			        WHERE item.purchase_order_id = po.id
			    ) AS itemCount,

			    po.subtotal AS subtotal,
			    po.tax_amount AS taxAmount,
			    po.delivery_fee AS deliveryFee,
			    po.grand_total AS grandTotal,

			    po.created_at AS createdAt

			FROM dt_purchase_orders_po po

			JOIN st_purchase_order_status_pos ps
			    ON ps.id = po.status_id

			JOIN dt_suppliers_sup sup
			    ON sup.id = po.supplier_id

			WHERE
			    (
			        :search IS NULL

			        OR LOWER(po.po_number)
			            LIKE LOWER(CONCAT('%', :search, '%'))

			        OR LOWER(sup.code)
			            LIKE LOWER(CONCAT('%', :search, '%'))

			        OR LOWER(sup.name)
			            LIKE LOWER(CONCAT('%', :search, '%'))
			    )

			    AND (
			        :statusCode IS NULL
			        OR UPPER(ps.code)
			            = UPPER(:statusCode)
			    )

			    AND (
			        :supplierId IS NULL
			        OR sup.id = :supplierId
			    )

			    AND (
			        :fromDate IS NULL
			        OR po.order_date >= :fromDate
			    )

			    AND (
			        :toDate IS NULL
			        OR po.order_date <= :toDate
			    )

			ORDER BY
			    po.order_date DESC,
			    po.id DESC
			""",

			countQuery = """
					SELECT COUNT(*)

					FROM dt_purchase_orders_po po

					JOIN st_purchase_order_status_pos ps
					    ON ps.id = po.status_id

					JOIN dt_suppliers_sup sup
					    ON sup.id = po.supplier_id

					WHERE
					    (
					        :search IS NULL

					        OR LOWER(po.po_number)
					            LIKE LOWER(CONCAT('%', :search, '%'))

					        OR LOWER(sup.code)
					            LIKE LOWER(CONCAT('%', :search, '%'))

					        OR LOWER(sup.name)
					            LIKE LOWER(CONCAT('%', :search, '%'))
					    )

					    AND (
					        :statusCode IS NULL
					        OR UPPER(ps.code)
					            = UPPER(:statusCode)
					    )

					    AND (
					        :supplierId IS NULL
					        OR sup.id = :supplierId
					    )

					    AND (
					        :fromDate IS NULL
					        OR po.order_date >= :fromDate
					    )

					    AND (
					        :toDate IS NULL
					        OR po.order_date <= :toDate
					    )
					""",

			nativeQuery = true)
	Page<PurchaseOrderSummaryProjection> findPurchaseOrderSummaries(

			@Param("search") String search,

			@Param("statusCode") String statusCode,

			@Param("supplierId") Long supplierId,

			@Param("fromDate") LocalDate fromDate,

			@Param("toDate") LocalDate toDate,

			Pageable pageable);
}
