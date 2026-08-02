package site.ethosylar.pos.ingredient.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.entity.IngredientSupplierEntity;
import site.ethosylar.pos.ingredient.response.IngredientDetailResponse;
import site.ethosylar.pos.ingredient.response.IngredientSummaryResponse;
import site.ethosylar.pos.ingredient.response.IngredientSupplierResponse;

@Component
public class IngredientMapper {

	public IngredientSummaryResponse toSummaryResponse(IngredientEntity entity) {

		return IngredientSummaryResponse.builder().id(entity.getId()).code(entity.getCode()).name(entity.getName())

				.categoryId(entity.getCategory().getId()).categoryCode(entity.getCategory().getCode())
				.categoryName(entity.getCategory().getName()).baseUnitId(entity.getBaseUnit().getId())
				.baseUnitCode(entity.getBaseUnit().getCode()).baseUnitName(entity.getBaseUnit().getName())
				.baseUnitSymbol(entity.getBaseUnit().getSymbol()).minimumStockQty(entity.getMinimumStockQty())
				.reorderQty(entity.getReorderQty()).active(entity.getActive()).createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt()).build();
	}

	public IngredientDetailResponse toDetailResponse(IngredientEntity entity,
			List<IngredientSupplierEntity> suppliers) {

		List<IngredientSupplierResponse> supplierResponses = suppliers.stream().map(this::toSupplierResponse).toList();

		BigDecimal preferredCost = suppliers.stream()
				.filter(supplier -> Boolean.TRUE.equals(supplier.getActive())
						&& Boolean.TRUE.equals(supplier.getPreferred()))
				.findFirst().map(this::calculateCostPerBaseUnit).orElse(null);

		return IngredientDetailResponse.builder().id(entity.getId()).code(entity.getCode()).name(entity.getName())

				.categoryId(entity.getCategory().getId()).categoryCode(entity.getCategory().getCode())
				.categoryName(entity.getCategory().getName())

				.baseUnitId(entity.getBaseUnit().getId()).baseUnitCode(entity.getBaseUnit().getCode())
				.baseUnitName(entity.getBaseUnit().getName()).baseUnitSymbol(entity.getBaseUnit().getSymbol())

				.minimumStockQty(entity.getMinimumStockQty()).reorderQty(entity.getReorderQty())

				.preferredCostPerBaseUnit(preferredCost)

				.notes(entity.getNotes()).active(entity.getActive())

				.suppliers(supplierResponses)

				.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
	}

	public IngredientSupplierResponse toSupplierResponse(IngredientSupplierEntity entity) {

		return IngredientSupplierResponse.builder().id(entity.getId())

				.supplierId(entity.getSupplier().getId()).supplierCode(entity.getSupplier().getCode())
				.supplierName(entity.getSupplier().getName())

				.supplierItemCode(entity.getSupplierItemCode())

				.purchaseUnitId(entity.getPurchaseUnit().getId()).purchaseUnitCode(entity.getPurchaseUnit().getCode())
				.purchaseUnitName(entity.getPurchaseUnit().getName())
				.purchaseUnitSymbol(entity.getPurchaseUnit().getSymbol())

				.baseQuantity(entity.getBaseQuantity()).purchasePrice(entity.getPurchasePrice())

				.costPerBaseUnit(calculateCostPerBaseUnit(entity))

				.preferred(entity.getPreferred()).active(entity.getActive())

				.notes(entity.getNotes())

				.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt())

				.build();
	}

	private BigDecimal calculateCostPerBaseUnit(IngredientSupplierEntity entity) {

		return entity.getPurchasePrice().divide(entity.getBaseQuantity(), 6, RoundingMode.HALF_UP);
	}
}