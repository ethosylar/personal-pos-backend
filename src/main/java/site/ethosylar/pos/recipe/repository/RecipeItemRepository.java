package site.ethosylar.pos.recipe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.recipe.entity.RecipeItemEntity;

public interface RecipeItemRepository extends JpaRepository<RecipeItemEntity, Long> {

	List<RecipeItemEntity> findByRecipe_IdOrderByDisplayOrderAscIdAsc(Long recipeId);

	Optional<RecipeItemEntity> findByIdAndRecipe_Id(Long id, Long recipeId);

	boolean existsByRecipe_IdAndIngredient_Id(Long recipeId, Long ingredientId);
}