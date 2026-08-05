package site.ethosylar.pos.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.inventory.entity.InventoryStockPolicyEntity;

public interface InventoryStockPolicyRepository extends JpaRepository<InventoryStockPolicyEntity, Long> {

	Optional<InventoryStockPolicyEntity> findByIngredient_Id(Long ingredientId);

	boolean existsByIngredient_Id(Long ingredientId);

	List<InventoryStockPolicyEntity> findByAlertEnabledTrue();
}