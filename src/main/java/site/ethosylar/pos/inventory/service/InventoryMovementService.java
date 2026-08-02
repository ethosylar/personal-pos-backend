package site.ethosylar.pos.inventory.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.repository.IngredientRepository;
import site.ethosylar.pos.inventory.entity.InventoryMovementEntity;
import site.ethosylar.pos.inventory.entity.InventoryMovementTypeEntity;
import site.ethosylar.pos.inventory.mapper.InventoryMapper;
import site.ethosylar.pos.inventory.repository.InventoryMovementRepository;
import site.ethosylar.pos.inventory.repository.InventoryMovementTypeRepository;
import site.ethosylar.pos.inventory.request.InventoryReturnRequest;
import site.ethosylar.pos.inventory.request.ManualInventoryMovementRequest;
import site.ethosylar.pos.inventory.request.StockAdjustmentRequest;
import site.ethosylar.pos.inventory.response.InventoryMovementResponse;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryMovementService {

	private final InventoryMovementRepository movementRepository;

	private final InventoryMovementTypeRepository movementTypeRepository;

	private final IngredientRepository ingredientRepository;

	private final UserRepository userRepository;

	private final InventoryMapper inventoryMapper;

	private final AuditService auditService;

	public InventoryMovementResponse recordOpeningStock(ManualInventoryMovementRequest request) {

		IngredientEntity ingredient = lockActiveIngredient(request.getIngredientId());

		if (movementRepository.existsByIngredient_Id(ingredient.getId())) {

			throw new BusinessRuleException(
					"Opening stock can only be recorded before any inventory movement exists for the ingredient.");
		}

		return createManualMovement(ingredient, "OPENING_STOCK", request.getQuantity(), request.getUnitCost(),
				request.getOccurredAt(), request.getReason(), request.getNotes());
	}

	public InventoryMovementResponse recordWaste(ManualInventoryMovementRequest request) {

		IngredientEntity ingredient = lockActiveIngredient(request.getIngredientId());

		ensureSufficientStock(ingredient.getId(), request.getQuantity());

		return createManualMovement(ingredient, "WASTE", request.getQuantity(), request.getUnitCost(),
				request.getOccurredAt(), request.getReason(), request.getNotes());
	}

	public InventoryMovementResponse recordManualUsage(ManualInventoryMovementRequest request) {

		IngredientEntity ingredient = lockActiveIngredient(request.getIngredientId());

		ensureSufficientStock(ingredient.getId(), request.getQuantity());

		return createManualMovement(ingredient, "MANUAL_USAGE", request.getQuantity(), request.getUnitCost(),
				request.getOccurredAt(), request.getReason(), request.getNotes());
	}

	public InventoryMovementResponse recordAdjustment(StockAdjustmentRequest request) {

		IngredientEntity ingredient = lockActiveIngredient(request.getIngredientId());

		String direction = normalizeDirection(request.getDirection());

		String typeCode;

		if ("IN".equals(direction)) {

			typeCode = "ADJUSTMENT_IN";

		} else {

			typeCode = "ADJUSTMENT_OUT";

			ensureSufficientStock(ingredient.getId(), request.getQuantity());
		}

		return createManualMovement(ingredient, typeCode, request.getQuantity(), request.getUnitCost(),
				request.getOccurredAt(), request.getReason(), request.getNotes());
	}

	public InventoryMovementResponse recordReturn(InventoryReturnRequest request) {

		IngredientEntity ingredient = lockActiveIngredient(request.getIngredientId());

		String direction = normalizeDirection(request.getDirection());

		String typeCode;

		if ("IN".equals(direction)) {

			typeCode = "RETURN_IN";

		} else {

			typeCode = "RETURN_OUT";

			ensureSufficientStock(ingredient.getId(), request.getQuantity());
		}

		return createManualMovement(ingredient, typeCode, request.getQuantity(), request.getUnitCost(),
				request.getOccurredAt(), request.getReason(), request.getNotes());
	}

	private InventoryMovementResponse createManualMovement(IngredientEntity ingredient, String movementTypeCode,
			BigDecimal quantity, BigDecimal unitCost, OffsetDateTime occurredAt, String reason, String notes) {

		InventoryMovementTypeEntity type = getManualMovementType(movementTypeCode);

		InventoryMovementEntity movement = new InventoryMovementEntity();

		movement.setIngredient(ingredient);
		movement.setMovementType(type);

		movement.setQuantity(quantity);
		movement.setUnitCost(unitCost);

		movement.setOccurredAt(occurredAt != null ? occurredAt : OffsetDateTime.now());

		movement.setReferenceType("MANUAL");

		movement.setReason(normalize(reason));

		movement.setNotes(normalize(notes));

		movement.setCreatedByUser(getCurrentUser());

		movement = movementRepository.save(movement);

		auditService.log(
				AuditLogRequest.builder().actionCode("CREATE").moduleCode("INVENTORY").entityType("INVENTORY_MOVEMENT")
						.entityId(movement.getId()).description("Inventory movement recorded: " + type.getCode() + ".")
						.afterData(snapshot(movement)).build());

		return inventoryMapper.toMovementResponse(movement);
	}

	private IngredientEntity lockActiveIngredient(Long ingredientId) {

		IngredientEntity ingredient = ingredientRepository.findByIdForUpdate(ingredientId)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));

		if (!Boolean.TRUE.equals(ingredient.getActive())) {

			throw new BusinessRuleException("Ingredient is inactive.");
		}

		return ingredient;
	}

	private InventoryMovementTypeEntity getManualMovementType(String code) {

		InventoryMovementTypeEntity type = movementTypeRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory movement type not found."));

		if (!Boolean.TRUE.equals(type.getActive())) {

			throw new BusinessRuleException("Inventory movement type is inactive.");
		}

		if (!Boolean.TRUE.equals(type.getManualAllowed())) {

			throw new BusinessRuleException("Inventory movement type cannot be created manually.");
		}

		return type;
	}

	private void ensureSufficientStock(Long ingredientId, BigDecimal requestedQuantity) {

		BigDecimal currentStock = movementRepository.calculateCurrentStock(ingredientId);

		if (currentStock.compareTo(requestedQuantity) < 0) {

			throw new BusinessRuleException("Insufficient stock. Current stock is " + currentStock
					+ ", requested stock-out quantity is " + requestedQuantity + ".");
		}
	}

	private UserEntity getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
	}

	private String normalizeDirection(String value) {

		String direction = value.trim().toUpperCase(Locale.ROOT);

		if (!direction.equals("IN") && !direction.equals("OUT")) {

			throw new BusinessRuleException("Direction must be either IN or OUT.");
		}

		return direction;
	}

	private Map<String, Object> snapshot(InventoryMovementEntity movement) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("ingredient", movement.getIngredient().getCode());

		data.put("movementType", movement.getMovementType().getCode());

		data.put("direction", movement.getMovementType().getDirection());

		data.put("quantity", movement.getQuantity());

		data.put("unitCost", movement.getUnitCost());

		data.put("occurredAt", movement.getOccurredAt());

		data.put("referenceType", movement.getReferenceType());

		data.put("reason", movement.getReason());

		return data;
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}
}