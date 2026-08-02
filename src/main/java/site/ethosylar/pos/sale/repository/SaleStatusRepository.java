package site.ethosylar.pos.sale.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.sale.entity.SaleStatusEntity;

public interface SaleStatusRepository extends JpaRepository<SaleStatusEntity, Long> {

	Optional<SaleStatusEntity> findByCodeIgnoreCase(String code);
}
