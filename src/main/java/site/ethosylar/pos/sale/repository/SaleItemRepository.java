package site.ethosylar.pos.sale.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.sale.entity.SaleItemEntity;

public interface SaleItemRepository extends JpaRepository<SaleItemEntity, Long> {

	List<SaleItemEntity> findBySale_IdOrderByIdAsc(Long saleId);

	Optional<SaleItemEntity> findByIdAndSale_Id(Long id, Long saleId);

	boolean existsBySale_IdAndProduct_Id(Long saleId, Long productId);
}
