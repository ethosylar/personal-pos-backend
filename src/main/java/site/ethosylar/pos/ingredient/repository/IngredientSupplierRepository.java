package site.ethosylar.pos.ingredient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.ingredient.entity.IngredientSupplierEntity;

public interface IngredientSupplierRepository extends JpaRepository<IngredientSupplierEntity, Long> {

	List<IngredientSupplierEntity> findByIngredient_IdOrderByPreferredDescSupplier_NameAsc(Long ingredientId);

	Optional<IngredientSupplierEntity> findByIngredient_IdAndSupplier_Id(Long ingredientId, Long supplierId);

	boolean existsByIngredient_IdAndSupplier_Id(Long ingredientId, Long supplierId);

	Optional<IngredientSupplierEntity> findByIdAndIngredient_Id(Long id, Long ingredientId);
}