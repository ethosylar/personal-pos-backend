package site.ethosylar.pos.ingredient.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.common.request.ChangeActiveStatusRequest;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.entity.IngredientSupplierEntity;
import site.ethosylar.pos.ingredient.mapper.IngredientMapper;
import site.ethosylar.pos.ingredient.repository.IngredientRepository;
import site.ethosylar.pos.ingredient.repository.IngredientSupplierRepository;
import site.ethosylar.pos.ingredient.request.CreateIngredientSupplierRequest;
import site.ethosylar.pos.ingredient.request.UpdateIngredientSupplierRequest;
import site.ethosylar.pos.ingredient.response.IngredientSupplierResponse;
import site.ethosylar.pos.supplier.entity.SupplierEntity;
import site.ethosylar.pos.supplier.repository.SupplierRepository;
import site.ethosylar.pos.unit.entity.UnitOfMeasureEntity;
import site.ethosylar.pos.unit.repository.UnitOfMeasureRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientSupplierService {

	private final IngredientRepository ingredientRepository;

	private final IngredientSupplierRepository ingredientSupplierRepository;

	private final SupplierRepository supplierRepository;

	private final UnitOfMeasureRepository unitRepository;

	private final IngredientMapper ingredientMapper;

	private final AuditService auditService;

	@Transactional(readOnly = true)
	public List<IngredientSupplierResponse> getSuppliers(Long ingredientId) {

		getIngredient(ingredientId);

		return ingredientSupplierRepository.findByIngredient_IdOrderByPreferredDescSupplier_NameAsc(ingredientId)
				.stream().map(ingredientMapper::toSupplierResponse).toList();
	}

	public IngredientSupplierResponse create(Long ingredientId, CreateIngredientSupplierRequest request) {

		IngredientEntity ingredient = getIngredient(ingredientId);

		SupplierEntity supplier = getActiveSupplier(request.getSupplierId());

		UnitOfMeasureEntity purchaseUnit = getActiveUnit(request.getPurchaseUnitId());

		if (ingredientSupplierRepository.existsByIngredient_IdAndSupplier_Id(ingredientId, supplier.getId())) {

			throw new DuplicateResourceException("Supplier is already assigned to this ingredient.");
		}

		IngredientSupplierEntity entity = new IngredientSupplierEntity();

		entity.setIngredient(ingredient);
		entity.setSupplier(supplier);

		entity.setSupplierItemCode(normalize(request.getSupplierItemCode()));

		entity.setPurchaseUnit(purchaseUnit);

		entity.setBaseQuantity(request.getBaseQuantity());

		entity.setPurchasePrice(request.getPurchasePrice());

		entity.setPreferred(Boolean.TRUE.equals(request.getPreferred()));

		entity.setActive(true);

		entity.setNotes(normalize(request.getNotes()));

		if (Boolean.TRUE.equals(entity.getPreferred())) {

			clearPreferredSupplier(ingredientId, null);
		}

		entity = ingredientSupplierRepository.save(entity);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("INGREDIENT")
				.entityType("INGREDIENT_SUPPLIER").entityId(entity.getId())
				.description("Ingredient supplier option created.").afterData(snapshot(entity)).build());

		return ingredientMapper.toSupplierResponse(entity);
	}

	public IngredientSupplierResponse update(Long ingredientId, Long supplierOptionId,
			UpdateIngredientSupplierRequest request) {

		getIngredient(ingredientId);

		IngredientSupplierEntity entity = getSupplierOption(supplierOptionId, ingredientId);

		Map<String, Object> before = snapshot(entity);

		UnitOfMeasureEntity purchaseUnit = getActiveUnit(request.getPurchaseUnitId());

		entity.setSupplierItemCode(normalize(request.getSupplierItemCode()));

		entity.setPurchaseUnit(purchaseUnit);

		entity.setBaseQuantity(request.getBaseQuantity());

		entity.setPurchasePrice(request.getPurchasePrice());

		entity.setNotes(normalize(request.getNotes()));

		boolean preferred = Boolean.TRUE.equals(request.getPreferred());

		if (preferred) {

			clearPreferredSupplier(ingredientId, entity.getId());
		}

		entity.setPreferred(preferred);

		ingredientSupplierRepository.save(entity);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("INGREDIENT")
				.entityType("INGREDIENT_SUPPLIER").entityId(entity.getId())
				.description("Ingredient supplier option updated.").beforeData(before).afterData(snapshot(entity))
				.build());

		return ingredientMapper.toSupplierResponse(entity);
	}

	public IngredientSupplierResponse changeStatus(Long ingredientId, Long supplierOptionId,
			ChangeActiveStatusRequest request) {

		getIngredient(ingredientId);

		IngredientSupplierEntity entity = getSupplierOption(supplierOptionId, ingredientId);

		Map<String, Object> before = snapshot(entity);

		entity.setActive(request.getActive());

		if (!Boolean.TRUE.equals(request.getActive())) {

			entity.setPreferred(false);
		}

		ingredientSupplierRepository.save(entity);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("INGREDIENT")
				.entityType("INGREDIENT_SUPPLIER").entityId(entity.getId())
				.description("Ingredient supplier status changed.").beforeData(before).afterData(snapshot(entity))
				.build());

		return ingredientMapper.toSupplierResponse(entity);
	}

	private void clearPreferredSupplier(Long ingredientId, Long exceptId) {

		List<IngredientSupplierEntity> suppliers = ingredientSupplierRepository
				.findByIngredient_IdOrderByPreferredDescSupplier_NameAsc(ingredientId);

		for (IngredientSupplierEntity supplier : suppliers) {

			if (!Boolean.TRUE.equals(supplier.getPreferred())) {
				continue;
			}

			if (exceptId != null && exceptId.equals(supplier.getId())) {
				continue;
			}

			supplier.setPreferred(false);

			ingredientSupplierRepository.save(supplier);
		}
	}

	private IngredientEntity getIngredient(Long id) {

		return ingredientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));
	}

	private IngredientSupplierEntity getSupplierOption(Long id, Long ingredientId) {

		return ingredientSupplierRepository.findByIdAndIngredient_Id(id, ingredientId)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient supplier option not found."));
	}

	private SupplierEntity getActiveSupplier(Long id) {

		SupplierEntity supplier = supplierRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

		if (!Boolean.TRUE.equals(supplier.getActive())) {

			throw new BusinessRuleException("Supplier is inactive.");
		}

		return supplier;
	}

	private UnitOfMeasureEntity getActiveUnit(Long id) {

		UnitOfMeasureEntity unit = unitRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found."));

		if (!Boolean.TRUE.equals(unit.getActive())) {

			throw new BusinessRuleException("Unit of measure is inactive.");
		}

		return unit;
	}

	private Map<String, Object> snapshot(IngredientSupplierEntity entity) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("ingredient", entity.getIngredient().getCode());

		data.put("supplier", entity.getSupplier().getCode());

		data.put("supplierItemCode", entity.getSupplierItemCode());

		data.put("purchaseUnit", entity.getPurchaseUnit().getCode());

		data.put("baseQuantity", entity.getBaseQuantity());

		data.put("purchasePrice", entity.getPurchasePrice());

		data.put("costPerBaseUnit", calculateCost(entity));

		data.put("preferred", entity.getPreferred());

		data.put("active", entity.getActive());

		return data;
	}

	private BigDecimal calculateCost(IngredientSupplierEntity entity) {

		return entity.getPurchasePrice().divide(entity.getBaseQuantity(), 6, RoundingMode.HALF_UP);
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}
}