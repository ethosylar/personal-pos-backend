package site.ethosylar.pos.recipe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import site.ethosylar.pos.recipe.entity.RecipeEntity;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long>, JpaSpecificationExecutor<RecipeEntity> {

	Optional<RecipeEntity> findByProduct_IdAndCurrentTrue(Long productId);

	Optional<RecipeEntity> findTopByProduct_IdOrderByVersionNoDesc(Long productId);

	boolean existsByProduct_IdAndCurrentTrue(Long productId);

	List<RecipeEntity> findByProduct_IdOrderByVersionNoDesc(Long productId);
}