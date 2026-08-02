package site.ethosylar.pos.category.service;

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
import site.ethosylar.pos.category.mapper.CategoryMapper;
import site.ethosylar.pos.category.repository.IngredientCategoryRepository;
import site.ethosylar.pos.category.request.CreateCategoryRequest;
import site.ethosylar.pos.category.request.UpdateCategoryRequest;
import site.ethosylar.pos.category.response.CategoryResponse;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.common.request.ChangeActiveStatusRequest;
import site.ethosylar.pos.common.request.MasterDataFilterRequest;
import site.ethosylar.pos.common.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientCategoryService {

	private final IngredientCategoryRepository repository;
	private final CategoryMapper mapper;
	private final AuditService auditService;

	@Transactional(readOnly = true)
	public PageResponse<CategoryResponse> search(MasterDataFilterRequest filter) {

		PageRequest pageable = PageRequest.of(filter.getPage() - 1, filter.getPerPage(),
				Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("name")));

		Specification<IngredientCategoryEntity> specification = buildSpecification(filter);

		Page<CategoryResponse> page = repository.findAll(specification, pageable).map(mapper::toResponse);

		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public CategoryResponse getById(Long id) {

		return mapper.toResponse(getEntity(id));
	}

	public CategoryResponse create(CreateCategoryRequest request) {

		String code = request.getCode().trim().toUpperCase(Locale.ROOT);

		if (repository.existsByCodeIgnoreCase(code)) {

			throw new DuplicateResourceException("Ingredient category code already exists.");
		}

		IngredientCategoryEntity entity = new IngredientCategoryEntity();

		entity.setCode(code);
		entity.setName(request.getName().trim());
		entity.setDescription(normalize(request.getDescription()));
		entity.setDisplayOrder(request.getDisplayOrder());
		entity.setActive(true);

		entity = repository.save(entity);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("CATEGORY")
				.entityType("INGREDIENT_CATEGORY").entityId(entity.getId()).description("Ingredient category created.")
				.afterData(snapshot(entity)).build());

		return mapper.toResponse(entity);
	}

	public CategoryResponse update(Long id, UpdateCategoryRequest request) {

		IngredientCategoryEntity entity = getEntity(id);

		Map<String, Object> before = snapshot(entity);

		entity.setName(request.getName().trim());

		entity.setDescription(normalize(request.getDescription()));

		entity.setDisplayOrder(request.getDisplayOrder());

		repository.save(entity);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("CATEGORY")
				.entityType("INGREDIENT_CATEGORY").entityId(entity.getId()).description("Ingredient category updated.")
				.beforeData(before).afterData(snapshot(entity)).build());

		return mapper.toResponse(entity);
	}

	public CategoryResponse changeStatus(Long id, ChangeActiveStatusRequest request) {

		IngredientCategoryEntity entity = getEntity(id);

		Map<String, Object> before = snapshot(entity);

		entity.setActive(request.getActive());

		repository.save(entity);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("CATEGORY")
				.entityType("INGREDIENT_CATEGORY").entityId(entity.getId()).description("Ingredient category status changed.")
				.beforeData(before).afterData(snapshot(entity)).build());

		return mapper.toResponse(entity);
	}

	private IngredientCategoryEntity getEntity(Long id) {

		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ingredient category not found."));
	}

	private Specification<IngredientCategoryEntity> buildSpecification(MasterDataFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			String search = normalize(filter.getSearch());

			if (search != null) {

				String term = "%" + search.toLowerCase(Locale.ROOT) + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.<String>get("code")), term),
						cb.like(cb.lower(root.<String>get("name")), term)));
			}

			if (filter.getActive() != null) {

				predicates.add(cb.equal(root.get("active"), filter.getActive()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Map<String, Object> snapshot(IngredientCategoryEntity entity) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("code", entity.getCode());
		data.put("name", entity.getName());
		data.put("description", entity.getDescription());
		data.put("displayOrder", entity.getDisplayOrder());
		data.put("active", entity.getActive());

		return data;
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}
}