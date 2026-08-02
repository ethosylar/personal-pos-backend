package site.ethosylar.pos.ingredient.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.ingredient.entity.IngredientSupplierEntity;
import site.ethosylar.pos.ingredient.repository.IngredientSupplierRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientCostService {

	private final IngredientSupplierRepository ingredientSupplierRepository;

	public Optional<BigDecimal> getPreferredCostPerBaseUnit(Long ingredientId) {

		return ingredientSupplierRepository.findFirstByIngredient_IdAndPreferredTrueAndActiveTrue(ingredientId)
				.map(this::calculateCostPerBaseUnit);
	}

	public BigDecimal calculateCostPerBaseUnit(IngredientSupplierEntity supplier) {

		return supplier.getPurchasePrice().divide(supplier.getBaseQuantity(), 6, RoundingMode.HALF_UP);
	}
}