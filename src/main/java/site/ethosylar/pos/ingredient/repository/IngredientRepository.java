package site.ethosylar.pos.ingredient.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import site.ethosylar.pos.ingredient.entity.IngredientEntity;

public interface IngredientRepository
		extends JpaRepository<IngredientEntity, Long>, JpaSpecificationExecutor<IngredientEntity> {

	Optional<IngredientEntity> findByCodeIgnoreCase(String code);

	boolean existsByCodeIgnoreCase(String code);
}