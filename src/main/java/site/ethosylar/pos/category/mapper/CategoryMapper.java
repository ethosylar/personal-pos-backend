package site.ethosylar.pos.category.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.category.entity.IngredientCategoryEntity;
import site.ethosylar.pos.category.entity.ProductCategoryEntity;
import site.ethosylar.pos.category.response.CategoryResponse;

@Component
public class CategoryMapper {

	public CategoryResponse toResponse(ProductCategoryEntity entity) {

		return CategoryResponse.builder().id(entity.getId()).code(entity.getCode()).name(entity.getName())
				.description(entity.getDescription()).displayOrder(entity.getDisplayOrder()).active(entity.getActive())
				.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
	}

	public CategoryResponse toResponse(IngredientCategoryEntity entity) {

		return CategoryResponse.builder().id(entity.getId()).code(entity.getCode()).name(entity.getName())
				.description(entity.getDescription()).displayOrder(entity.getDisplayOrder()).active(entity.getActive())
				.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
	}
}