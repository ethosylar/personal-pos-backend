package site.ethosylar.pos.inventory.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.inventory.entity.InventoryStockPolicyEntity;
import site.ethosylar.pos.inventory.response.InventoryStockPolicyResponse;

@Component
public class InventoryStockPolicyMapper {

	public InventoryStockPolicyResponse toResponse(InventoryStockPolicyEntity policy) {

		return InventoryStockPolicyResponse.builder()

				.id(policy.getId())

				.ingredientId(policy.getIngredient().getId())

				.ingredientCode(policy.getIngredient().getCode())

				.ingredientName(policy.getIngredient().getName())

				.reorderLevel(policy.getReorderLevel())

				.targetStockLevel(policy.getTargetStockLevel())

				.alertEnabled(policy.getAlertEnabled())

				.notes(policy.getNotes())

				.createdAt(policy.getCreatedAt())

				.updatedAt(policy.getUpdatedAt())

				.build();
	}
}