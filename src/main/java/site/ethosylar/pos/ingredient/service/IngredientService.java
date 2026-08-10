package site.ethosylar.pos.ingredient.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.category.entity.IngredientCategoryEntity;
import site.ethosylar.pos.category.repository.IngredientCategoryRepository;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.common.request.ChangeActiveStatusRequest;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.mapper.IngredientMapper;
import site.ethosylar.pos.ingredient.projection.IngredientSummaryProjection;
import site.ethosylar.pos.ingredient.repository.IngredientRepository;
import site.ethosylar.pos.ingredient.repository.IngredientSupplierRepository;
import site.ethosylar.pos.ingredient.request.CreateIngredientRequest;
import site.ethosylar.pos.ingredient.request.IngredientFilterRequest;
import site.ethosylar.pos.ingredient.request.UpdateIngredientRequest;
import site.ethosylar.pos.ingredient.response.IngredientDetailResponse;
import site.ethosylar.pos.ingredient.response.IngredientSummaryResponse;
import site.ethosylar.pos.unit.entity.UnitOfMeasureEntity;
import site.ethosylar.pos.unit.repository.UnitOfMeasureRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientService {

	private final IngredientRepository ingredientRepository;
	private final IngredientCategoryRepository categoryRepository;
	private final UnitOfMeasureRepository unitRepository;

	private final IngredientSupplierRepository ingredientSupplierRepository;

	private final IngredientMapper ingredientMapper;
	private final AuditService auditService;

	@Transactional(readOnly = true)
	public PageResponse<IngredientSummaryResponse> search(IngredientFilterRequest filter) {

		int pageIndex = Math.max(filter.getPage() - 1, 0);

		PageRequest pageable = PageRequest.of(pageIndex, filter.getPerPage(), Sort.by(Sort.Direction.ASC, "name"));

		Page<IngredientSummaryResponse> page = ingredientRepository.findAll(buildSpecification(filter), pageable)
				.map(ingredientMapper::toSummaryResponse);

		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public IngredientDetailResponse getById(Long id) {

		IngredientEntity ingredient = getEntity(id);

		return buildDetailResponse(ingredient);
	}

	public IngredientDetailResponse create(CreateIngredientRequest request) {

		String code = request.getCode().trim().toUpperCase(Locale.ROOT);

		if (ingredientRepository.existsByCodeIgnoreCase(code)) {

			throw new DuplicateResourceException("Ingredient code already exists.");
		}

		IngredientCategoryEntity category = getActiveCategory(request.getIngredientCategoryId());

		UnitOfMeasureEntity baseUnit = getValidBaseUnit(request.getBaseUnitId());

		IngredientEntity ingredient = new IngredientEntity();

		ingredient.setCode(code);
		ingredient.setName(request.getName().trim());
		ingredient.setCategory(category);
		ingredient.setBaseUnit(baseUnit);

		ingredient.setMinimumStockQty(request.getMinimumStockQty());

		ingredient.setReorderQty(request.getReorderQty());

		ingredient.setNotes(normalize(request.getNotes()));

		ingredient.setActive(true);

		ingredient = ingredientRepository.save(ingredient);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("INGREDIENT")
				.entityType("INGREDIENT").entityId(ingredient.getId()).description("Ingredient created.")
				.afterData(snapshot(ingredient)).build());

		return buildDetailResponse(ingredient);
	}

	public IngredientDetailResponse update(Long id, UpdateIngredientRequest request) {

		IngredientEntity ingredient = getEntity(id);

		Map<String, Object> before = snapshot(ingredient);

		IngredientCategoryEntity category = getActiveCategory(request.getIngredientCategoryId());

		ingredient.setName(request.getName().trim());

		ingredient.setCategory(category);

		ingredient.setMinimumStockQty(request.getMinimumStockQty());

		ingredient.setReorderQty(request.getReorderQty());

		ingredient.setNotes(normalize(request.getNotes()));

		ingredientRepository.save(ingredient);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("INGREDIENT")
				.entityType("INGREDIENT").entityId(ingredient.getId()).description("Ingredient updated.")
				.beforeData(before).afterData(snapshot(ingredient)).build());

		return buildDetailResponse(ingredient);
	}

	public IngredientDetailResponse changeStatus(Long id, ChangeActiveStatusRequest request) {

		IngredientEntity ingredient = getEntity(id);

		Map<String, Object> before = snapshot(ingredient);

		ingredient.setActive(request.getActive());

		ingredientRepository.save(ingredient);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("INGREDIENT")
				.entityType("INGREDIENT").entityId(ingredient.getId()).description("Ingredient status changed.")
				.beforeData(before).afterData(snapshot(ingredient)).build());

		return buildDetailResponse(ingredient);
	}

	private IngredientDetailResponse buildDetailResponse(IngredientEntity ingredient) {

		return ingredientMapper.toDetailResponse(ingredient, ingredientSupplierRepository
				.findByIngredient_IdOrderByPreferredDescSupplier_NameAsc(ingredient.getId()));
	}

	private IngredientEntity getEntity(Long id) {

		return ingredientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));
	}

	private IngredientCategoryEntity getActiveCategory(Long id) {

		IngredientCategoryEntity category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient category not found."));

		if (!Boolean.TRUE.equals(category.getActive())) {

			throw new BusinessRuleException("Ingredient category is inactive.");
		}

		return category;
	}

	private UnitOfMeasureEntity getValidBaseUnit(Long id) {

		UnitOfMeasureEntity unit = unitRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found."));

		if (!Boolean.TRUE.equals(unit.getActive())) {

			throw new BusinessRuleException("Unit of measure is inactive.");
		}

		if (!Boolean.TRUE.equals(unit.getBaseUnit())) {

			throw new BusinessRuleException("Ingredient base unit must be a base unit.");
		}

		return unit;
	}

	private Specification<IngredientEntity> buildSpecification(IngredientFilterRequest filter) {

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

	private Map<String, Object> snapshot(IngredientEntity entity) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("code", entity.getCode());
		data.put("name", entity.getName());

		data.put("category", entity.getCategory().getCode());

		data.put("baseUnit", entity.getBaseUnit().getCode());

		data.put("minimumStockQty", entity.getMinimumStockQty());

		data.put("reorderQty", entity.getReorderQty());

		data.put("active", entity.getActive());

		return data;
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}

	private IngredientSummaryResponse toSummary(IngredientSummaryProjection row) {

		BigDecimal unitCost = BigDecimal.ZERO;

		if (row.getPurchasePrice() != null && row.getSupplierBaseQuantity() != null
				&& row.getSupplierBaseQuantity().compareTo(BigDecimal.ZERO) > 0) {

			unitCost = row.getPurchasePrice().divide(row.getSupplierBaseQuantity(), 6, RoundingMode.HALF_UP);
		}

		return IngredientSummaryResponse.builder()

				.id(row.getId())

				.code(row.getCode())

				.name(row.getName())

				.baseUnitId(row.getBaseUnitId())

				.baseUnitCode(row.getBaseUnitCode())

				.baseUnitName(row.getBaseUnitName())

				.preferredSupplierId(row.getPreferredSupplierId())

				.preferredSupplierName(row.getPreferredSupplierName())

				.currentUnitCost(unitCost)

				.active(row.getActive())

				.build();
	}

	@Transactional(readOnly = true)
	public PageResponse<IngredientSummaryResponse> getIngredients(String search, int page, int perPage) {

		int safePage = Math.max(page, 1);

		int safePerPage = Math.min(Math.max(perPage, 1), 100);

		PageRequest pageable = PageRequest.of(safePage - 1, safePerPage, Sort.by("name").ascending());

		String normalizedSearch = normalize(search);

		Page<IngredientSummaryProjection> result = ingredientRepository.findIngredientSummaries(normalizedSearch,
				pageable);

		Page<IngredientSummaryResponse> mapped = result.map(this::toSummary);

		return PageResponse.from(mapped);
	}
}