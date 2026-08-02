package site.ethosylar.pos.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import site.ethosylar.pos.category.entity.IngredientCategoryEntity;

public interface IngredientCategoryRepository
        extends JpaRepository<IngredientCategoryEntity, Long>,
                JpaSpecificationExecutor<IngredientCategoryEntity> {

    Optional<IngredientCategoryEntity>
        findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}