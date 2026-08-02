package site.ethosylar.pos.inventory.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.repository.IngredientRepository;
import site.ethosylar.pos.inventory.entity.InventoryMovementEntity;
import site.ethosylar.pos.inventory.entity.InventoryMovementTypeEntity;
import site.ethosylar.pos.inventory.repository.InventoryMovementRepository;
import site.ethosylar.pos.inventory.repository.InventoryMovementTypeRepository;
import site.ethosylar.pos.user.entity.UserEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryPostingService {

	private final InventoryMovementRepository movementRepository;

	private final InventoryMovementTypeRepository movementTypeRepository;

	private final IngredientRepository ingredientRepository;

	public InventoryMovementEntity recordPurchaseReceipt(Long ingredientId, BigDecimal quantity, BigDecimal unitCost,
			OffsetDateTime occurredAt, Long goodsReceiptItemId, String receiptNumber, UserEntity user) {

		IngredientEntity ingredient = ingredientRepository.findByIdForUpdate(ingredientId)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));

		InventoryMovementTypeEntity type = movementTypeRepository.findByCodeIgnoreCase("PURCHASE_RECEIPT")
				.orElseThrow(() -> new ResourceNotFoundException("PURCHASE_RECEIPT movement type not found."));

		boolean alreadyPosted = movementRepository
				.existsByReferenceTypeIgnoreCaseAndReferenceIdAndIngredient_IdAndMovementType_Id("GOODS_RECEIPT_ITEM",
						goodsReceiptItemId, ingredientId, type.getId());

		if (alreadyPosted) {

			throw new DuplicateResourceException("Goods receipt item has already been posted to inventory.");
		}

		InventoryMovementEntity movement = new InventoryMovementEntity();

		movement.setIngredient(ingredient);
		movement.setMovementType(type);

		movement.setQuantity(quantity);
		movement.setUnitCost(unitCost);

		movement.setOccurredAt(occurredAt);

		movement.setReferenceType("GOODS_RECEIPT_ITEM");

		movement.setReferenceId(goodsReceiptItemId);

		movement.setReferenceNo(receiptNumber);

		movement.setReason("Goods receipt");

		movement.setCreatedByUser(user);

		return movementRepository.save(movement);
	}

	public InventoryMovementEntity recordSaleUsage(Long ingredientId, BigDecimal quantity, BigDecimal unitCost,
			OffsetDateTime occurredAt, Long saleItemId, String saleNumber, UserEntity user) {

		IngredientEntity ingredient = ingredientRepository.findByIdForUpdate(ingredientId)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));

		InventoryMovementTypeEntity type = movementTypeRepository.findByCodeIgnoreCase("SALE_USAGE")
				.orElseThrow(() -> new ResourceNotFoundException("SALE_USAGE movement type not found."));

		boolean exists = movementRepository
				.existsByReferenceTypeIgnoreCaseAndReferenceIdAndIngredient_IdAndMovementType_Id("SALE_ITEM",
						saleItemId, ingredientId, type.getId());

		if (exists) {
			throw new DuplicateResourceException("Sale ingredient usage has already been posted.");
		}

		InventoryMovementEntity movement = new InventoryMovementEntity();

		movement.setIngredient(ingredient);
		movement.setMovementType(type);

		movement.setQuantity(quantity);
		movement.setUnitCost(unitCost);

		movement.setOccurredAt(occurredAt);

		movement.setReferenceType("SALE_ITEM");
		movement.setReferenceId(saleItemId);
		movement.setReferenceNo(saleNumber);

		movement.setReason("POS sale consumption");

		movement.setCreatedByUser(user);

		return movementRepository.save(movement);
	}
}