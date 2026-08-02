package site.ethosylar.pos.inventory.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.inventory.constant.InventoryStockStatus;
import site.ethosylar.pos.inventory.entity.InventoryMovementEntity;
import site.ethosylar.pos.inventory.entity.InventoryMovementTypeEntity;
import site.ethosylar.pos.inventory.response.InventoryBalanceResponse;
import site.ethosylar.pos.inventory.response.InventoryMovementResponse;
import site.ethosylar.pos.inventory.response.InventoryMovementTypeResponse;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;

@Component
public class InventoryMapper {

	public InventoryMovementTypeResponse toMovementTypeResponse(InventoryMovementTypeEntity entity) {

		return InventoryMovementTypeResponse.builder().id(entity.getId()).code(entity.getCode()).name(entity.getName())
				.description(entity.getDescription()).direction(entity.getDirection().intValue())
				.manualAllowed(entity.getManualAllowed()).build();
	}

	public InventoryMovementResponse toMovementResponse(InventoryMovementEntity entity) {

		BigDecimal signedQuantity = entity.getQuantity()
				.multiply(BigDecimal.valueOf(entity.getMovementType().getDirection().longValue()));

		BigDecimal movementValue = null;

		if (entity.getUnitCost() != null) {

			movementValue = entity.getQuantity().multiply(entity.getUnitCost()).setScale(6, RoundingMode.HALF_UP);
		}

		return InventoryMovementResponse.builder().id(entity.getId())

				.ingredientId(entity.getIngredient().getId()).ingredientCode(entity.getIngredient().getCode())
				.ingredientName(entity.getIngredient().getName())

				.baseUnitCode(entity.getIngredient().getBaseUnit().getCode())
				.baseUnitSymbol(entity.getIngredient().getBaseUnit().getSymbol())

				.movementTypeId(entity.getMovementType().getId()).movementTypeCode(entity.getMovementType().getCode())
				.movementTypeName(entity.getMovementType().getName())

				.direction(entity.getMovementType().getDirection().intValue())

				.quantity(entity.getQuantity()).signedQuantity(signedQuantity)

				.unitCost(entity.getUnitCost()).movementValue(movementValue)

				.occurredAt(entity.getOccurredAt())

				.referenceType(entity.getReferenceType()).referenceId(entity.getReferenceId())
				.referenceNo(entity.getReferenceNo())

				.reason(entity.getReason()).notes(entity.getNotes())

				.createdByUserId(entity.getCreatedByUser() != null ? entity.getCreatedByUser().getId() : null)
				.createdByUsername(entity.getCreatedByUser() != null ? entity.getCreatedByUser().getUsername() : null)

				.createdAt(entity.getCreatedAt()).build();
	}

	public InventoryBalanceResponse toBalanceResponse(IngredientEntity ingredient, BigDecimal currentStock,
			BigDecimal estimatedUnitCost) {

		InventoryStockStatus status = resolveStatus(currentStock, ingredient.getMinimumStockQty());

		BigDecimal estimatedStockValue = null;

		if (estimatedUnitCost != null) {

			estimatedStockValue = currentStock.multiply(estimatedUnitCost).setScale(6, RoundingMode.HALF_UP);
		}

		return InventoryBalanceResponse.builder().ingredientId(ingredient.getId()).ingredientCode(ingredient.getCode())
				.ingredientName(ingredient.getName())

				.categoryId(ingredient.getCategory().getId()).categoryCode(ingredient.getCategory().getCode())
				.categoryName(ingredient.getCategory().getName())

				.baseUnitId(ingredient.getBaseUnit().getId()).baseUnitCode(ingredient.getBaseUnit().getCode())
				.baseUnitSymbol(ingredient.getBaseUnit().getSymbol())

				.currentStock(currentStock)

				.minimumStockQty(ingredient.getMinimumStockQty())

				.reorderQty(ingredient.getReorderQty())

				.stockStatus(status)

				.reorderSuggested(status != InventoryStockStatus.IN_STOCK)

				.currentEstimatedUnitCost(estimatedUnitCost)

				.estimatedStockValue(estimatedStockValue)

				.active(ingredient.getActive())

				.build();
	}

	private InventoryStockStatus resolveStatus(BigDecimal currentStock, BigDecimal minimumStock) {

		if (currentStock.compareTo(BigDecimal.ZERO) <= 0) {

			return InventoryStockStatus.OUT_OF_STOCK;
		}

		if (currentStock.compareTo(minimumStock) <= 0) {

			return InventoryStockStatus.LOW_STOCK;
		}

		return InventoryStockStatus.IN_STOCK;
	}
}