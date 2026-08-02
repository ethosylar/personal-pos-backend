package site.ethosylar.pos.ingredient.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.ethosylar.pos.ingredient.entity.IngredientEntity;

public interface IngredientRepository
		extends JpaRepository<IngredientEntity, Long>, JpaSpecificationExecutor<IngredientEntity> {

	Optional<IngredientEntity> findByCodeIgnoreCase(String code);

	boolean existsByCodeIgnoreCase(String code);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT i
			FROM IngredientEntity i
			WHERE i.id = :id
			""")
	Optional<IngredientEntity> findByIdForUpdate(@Param("id") Long id);
}