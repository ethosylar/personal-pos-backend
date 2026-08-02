package site.ethosylar.pos.inventory.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.repository.IngredientRepository;
import site.ethosylar.pos.ingredient.service.IngredientCostService;
import site.ethosylar.pos.inventory.entity.InventoryMovementEntity;
import site.ethosylar.pos.inventory.mapper.InventoryMapper;
import site.ethosylar.pos.inventory.repository.InventoryMovementRepository;
import site.ethosylar.pos.inventory.request.InventoryBalanceFilterRequest;
import site.ethosylar.pos.inventory.request.InventoryMovementFilterRequest;
import site.ethosylar.pos.inventory.response.InventoryBalanceResponse;
import site.ethosylar.pos.inventory.response.InventoryMovementResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryQueryService {

	private final InventoryMovementRepository movementRepository;

	private final IngredientRepository ingredientRepository;

	private final IngredientCostService ingredientCostService;

	private final InventoryMapper inventoryMapper;

	public PageResponse<InventoryMovementResponse> searchMovements(InventoryMovementFilterRequest filter) {

		PageRequest pageable = PageRequest.of(filter.getPage() - 1, filter.getPerPage(),
				Sort.by(Sort.Direction.DESC, "occurredAt"));

		Page<InventoryMovementResponse> page = movementRepository.findAll(buildMovementSpecification(filter), pageable)
				.map(inventoryMapper::toMovementResponse);

		return PageResponse.from(page);
	}

	public PageResponse<InventoryBalanceResponse> searchBalances(InventoryBalanceFilterRequest filter) {

		PageRequest pageable = PageRequest.of(filter.getPage() - 1, filter.getPerPage(),
				Sort.by(Sort.Direction.ASC, "name"));

		Page<InventoryBalanceResponse> page = ingredientRepository
				.findAll(buildIngredientSpecification(filter), pageable).map(this::buildBalance);

		return PageResponse.from(page);
	}

	public InventoryBalanceResponse getBalance(Long ingredientId) {

		IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));

		return buildBalance(ingredient);
	}

	private InventoryBalanceResponse buildBalance(IngredientEntity ingredient) {

		BigDecimal currentStock = movementRepository.calculateCurrentStock(ingredient.getId());

		BigDecimal estimatedUnitCost = ingredientCostService.getPreferredCostPerBaseUnit(ingredient.getId())
				.orElse(null);

		return inventoryMapper.toBalanceResponse(ingredient, currentStock, estimatedUnitCost);
	}

	private Specification<IngredientEntity> buildIngredientSpecification(InventoryBalanceFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			String search = normalize(filter.getSearch());

			if (search != null) {

				String term = "%" + search.toLowerCase(Locale.ROOT) + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.<String>get("code")), term),
						cb.like(cb.lower(root.<String>get("name")), term)));
			}

			if (filter.getCategoryId() != null) {

				predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
			}

			if (filter.getActive() != null) {

				predicates.add(cb.equal(root.get("active"), filter.getActive()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Specification<InventoryMovementEntity> buildMovementSpecification(InventoryMovementFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (filter.getIngredientId() != null) {

				predicates.add(cb.equal(root.get("ingredient").get("id"), filter.getIngredientId()));
			}

			if (filter.getMovementTypeCode() != null && !filter.getMovementTypeCode().isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("movementType").get("code")),
						filter.getMovementTypeCode().trim().toLowerCase(Locale.ROOT)));
			}

			if (filter.getReferenceType() != null && !filter.getReferenceType().isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("referenceType")),
						filter.getReferenceType().trim().toLowerCase(Locale.ROOT)));
			}

			if (filter.getFromDate() != null) {

				predicates.add(cb.greaterThanOrEqualTo(root.get("occurredAt"), filter.getFromDate()));
			}

			if (filter.getToDate() != null) {

				predicates.add(cb.lessThanOrEqualTo(root.get("occurredAt"), filter.getToDate()));
			}

			String search = normalize(filter.getSearch());

			if (search != null) {

				String term = "%" + search.toLowerCase(Locale.ROOT) + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.get("ingredient").get("code")), term),
						cb.like(cb.lower(root.get("ingredient").get("name")), term),
						cb.like(cb.lower(root.<String>get("referenceNo")), term),
						cb.like(cb.lower(root.<String>get("reason")), term)));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}
}