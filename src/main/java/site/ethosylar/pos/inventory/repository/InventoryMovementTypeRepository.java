package site.ethosylar.pos.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.inventory.entity.InventoryMovementTypeEntity;

public interface InventoryMovementTypeRepository extends JpaRepository<InventoryMovementTypeEntity, Long> {

	Optional<InventoryMovementTypeEntity> findByCodeIgnoreCase(String code);

	List<InventoryMovementTypeEntity> findByActiveTrueOrderByDisplayOrderAscNameAsc();
}