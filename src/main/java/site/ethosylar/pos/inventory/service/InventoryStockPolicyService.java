package site.ethosylar.pos.inventory.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.repository.IngredientRepository;
import site.ethosylar.pos.inventory.entity.InventoryStockPolicyEntity;
import site.ethosylar.pos.inventory.mapper.InventoryStockPolicyMapper;
import site.ethosylar.pos.inventory.repository.InventoryStockPolicyRepository;
import site.ethosylar.pos.inventory.request.UpdateStockPolicyRequest;
import site.ethosylar.pos.inventory.response.InventoryStockPolicyResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryStockPolicyService {

	private final InventoryStockPolicyRepository policyRepository;

	private final IngredientRepository ingredientRepository;

	private final InventoryStockPolicyMapper mapper;

	public InventoryStockPolicyResponse updatePolicy(Long ingredientId, UpdateStockPolicyRequest request) {

		IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));

		if (request.getTargetStockLevel() != null
				&& request.getTargetStockLevel().compareTo(request.getReorderLevel()) < 0) {

			throw new BusinessRuleException("Target stock level cannot be lower than reorder level.");
		}

		InventoryStockPolicyEntity policy = policyRepository.findByIngredient_Id(ingredientId).orElseGet(() -> {

			InventoryStockPolicyEntity entity = new InventoryStockPolicyEntity();

			entity.setIngredient(ingredient);

			return entity;
		});

		policy.setReorderLevel(request.getReorderLevel());

		policy.setTargetStockLevel(request.getTargetStockLevel());

		policy.setAlertEnabled(request.getAlertEnabled());

		policy.setNotes(normalize(request.getNotes()));

		policy = policyRepository.save(policy);

		return mapper.toResponse(policy);
	}

	@Transactional(readOnly = true)
	public InventoryStockPolicyResponse getPolicy(Long ingredientId) {

		InventoryStockPolicyEntity policy = policyRepository.findByIngredient_Id(ingredientId)
				.orElseThrow(() -> new ResourceNotFoundException("Stock policy not configured for this ingredient."));

		return mapper.toResponse(policy);
	}

	@Transactional(readOnly = true)
	public List<InventoryStockPolicyResponse> getPolicies() {

		return policyRepository.findAll().stream().map(mapper::toResponse).toList();
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}

}