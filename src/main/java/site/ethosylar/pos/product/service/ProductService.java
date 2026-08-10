package site.ethosylar.pos.product.service;

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
import site.ethosylar.pos.category.entity.ProductCategoryEntity;
import site.ethosylar.pos.category.repository.ProductCategoryRepository;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.common.request.ChangeActiveStatusRequest;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.product.entity.ProductEntity;
import site.ethosylar.pos.product.mapper.ProductMapper;
import site.ethosylar.pos.product.projection.ProductSummaryProjection;
import site.ethosylar.pos.product.repository.ProductRepository;
import site.ethosylar.pos.product.request.ChangeProductAvailabilityRequest;
import site.ethosylar.pos.product.request.ChangeProductPriceRequest;
import site.ethosylar.pos.product.request.CreateProductRequest;
import site.ethosylar.pos.product.request.ProductFilterRequest;
import site.ethosylar.pos.product.request.UpdateProductRequest;
import site.ethosylar.pos.product.response.ProductResponse;
import site.ethosylar.pos.product.response.ProductSummaryResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

	private final ProductRepository productRepository;

	private final ProductCategoryRepository categoryRepository;

	private final ProductMapper productMapper;

	private final AuditService auditService;

	@Transactional(readOnly = true)
	public PageResponse<ProductResponse> search(ProductFilterRequest filter) {

		int pageIndex = Math.max(filter.getPage() - 1, 0);

		PageRequest pageable = PageRequest.of(pageIndex, filter.getPerPage(),
				Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("name")));

		Page<ProductResponse> page = productRepository.findAll(buildSpecification(filter), pageable)
				.map(productMapper::toResponse);

		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public ProductResponse getById(Long id) {

		return productMapper.toResponse(getEntity(id));
	}

	public ProductResponse create(CreateProductRequest request) {

		String code = request.getCode().trim().toUpperCase(Locale.ROOT);

		if (productRepository.existsByCodeIgnoreCase(code)) {

			throw new DuplicateResourceException("Product code already exists.");
		}

		validateSku(null, request.getSku());
		validateBarcode(null, request.getBarcode());

		ProductCategoryEntity category = getActiveCategory(request.getProductCategoryId());

		ProductEntity product = new ProductEntity();

		product.setCode(code);
		product.setName(request.getName().trim());

		product.setCategory(category);

		product.setSku(normalize(request.getSku()));

		product.setBarcode(normalize(request.getBarcode()));

		product.setDescription(normalize(request.getDescription()));

		product.setSellingPrice(request.getSellingPrice());

		product.setDisplayOrder(request.getDisplayOrder());

		product.setAvailable(true);
		product.setActive(true);

		product = productRepository.save(product);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("PRODUCT").entityType("PRODUCT")
				.entityId(product.getId()).description("Product created.").afterData(snapshot(product)).build());

		return productMapper.toResponse(product);
	}

	public ProductResponse update(Long id, UpdateProductRequest request) {

		ProductEntity product = getEntity(id);

		Map<String, Object> before = snapshot(product);

		validateSku(id, request.getSku());
		validateBarcode(id, request.getBarcode());

		ProductCategoryEntity category = getActiveCategory(request.getProductCategoryId());

		product.setName(request.getName().trim());

		product.setCategory(category);

		product.setSku(normalize(request.getSku()));

		product.setBarcode(normalize(request.getBarcode()));

		product.setDescription(normalize(request.getDescription()));

		product.setDisplayOrder(request.getDisplayOrder());

		productRepository.save(product);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("PRODUCT").entityType("PRODUCT")
				.entityId(product.getId()).description("Product updated.").beforeData(before)
				.afterData(snapshot(product)).build());

		return productMapper.toResponse(product);
	}

	private void validateBarcode(Long productId, String barcode) {

		String normalized = normalize(barcode);

		if (normalized == null) {
			return;
		}

		boolean exists = productId == null ? productRepository.existsByBarcode(normalized)
				: productRepository.existsByBarcodeAndIdNot(normalized, productId);

		if (exists) {
			throw new DuplicateResourceException("Product barcode already exists.");
		}
	}

	public ProductResponse changePrice(Long id, ChangeProductPriceRequest request) {

		ProductEntity product = getEntity(id);

		Map<String, Object> before = new LinkedHashMap<>();

		before.put("sellingPrice", product.getSellingPrice());

		product.setSellingPrice(request.getSellingPrice());

		productRepository.save(product);

		Map<String, Object> after = new LinkedHashMap<>();

		after.put("sellingPrice", product.getSellingPrice());

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("PRODUCT")
				.entityType("PRODUCT_PRICE").entityId(product.getId()).description("Product selling price changed.")
				.beforeData(before).afterData(after).build());

		return productMapper.toResponse(product);
	}

	public ProductResponse changeAvailability(Long id, ChangeProductAvailabilityRequest request) {

		ProductEntity product = getEntity(id);

		Map<String, Object> before = new LinkedHashMap<>();

		before.put("available", product.getAvailable());

		product.setAvailable(request.getAvailable());

		productRepository.save(product);

		Map<String, Object> after = new LinkedHashMap<>();

		after.put("available", product.getAvailable());

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("PRODUCT")
				.entityType("PRODUCT").entityId(product.getId()).description("Product availability changed.")
				.beforeData(before).afterData(after).build());

		return productMapper.toResponse(product);
	}

	private ProductCategoryEntity getActiveCategory(Long id) {

		ProductCategoryEntity category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product category not found."));

		if (!Boolean.TRUE.equals(category.getActive())) {

			throw new BusinessRuleException("Product category is inactive.");
		}

		return category;
	}

	private void validateSku(Long productId, String sku) {

		String normalized = normalize(sku);

		if (normalized == null) {
			return;
		}

		boolean exists = productId == null ? productRepository.existsBySkuIgnoreCase(normalized)
				: productRepository.existsBySkuIgnoreCaseAndIdNot(normalized, productId);

		if (exists) {

			throw new DuplicateResourceException("Product SKU already exists.");
		}
	}

	private Specification<ProductEntity> buildSpecification(ProductFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			String search = normalize(filter.getSearch());

			if (search != null) {

				String term = "%" + search.toLowerCase(Locale.ROOT) + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.<String>get("code")), term),
						cb.like(cb.lower(root.<String>get("name")), term),
						cb.like(cb.lower(root.<String>get("sku")), term),
						cb.like(cb.lower(root.<String>get("barcode")), term)));
			}

			if (filter.getCategoryId() != null) {

				predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
			}

			if (filter.getActive() != null) {

				predicates.add(cb.equal(root.get("active"), filter.getActive()));
			}

			if (filter.getAvailable() != null) {

				predicates.add(cb.equal(root.get("available"), filter.getAvailable()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private ProductEntity getEntity(Long id) {

		return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."));
	}

	public ProductResponse changeStatus(Long id, ChangeActiveStatusRequest request) {

		ProductEntity product = getEntity(id);

		Map<String, Object> before = new LinkedHashMap<>();

		before.put("active", product.getActive());

		before.put("available", product.getAvailable());

		product.setActive(request.getActive());

		/*
		 * A disabled/discontinued product should not remain available for POS sales.
		 */
		if (!Boolean.TRUE.equals(request.getActive())) {

			product.setAvailable(false);
		}

		productRepository.save(product);

		Map<String, Object> after = new LinkedHashMap<>();

		after.put("active", product.getActive());

		after.put("available", product.getAvailable());

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("PRODUCT")
				.entityType("PRODUCT").entityId(product.getId()).description("Product status changed.")
				.beforeData(before).afterData(after).build());

		return productMapper.toResponse(product);
	}

	private Map<String, Object> snapshot(ProductEntity entity) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("code", entity.getCode());
		data.put("name", entity.getName());

		data.put("category", entity.getCategory() != null ? entity.getCategory().getCode() : null);

		data.put("sku", entity.getSku());
		data.put("barcode", entity.getBarcode());

		data.put("description", entity.getDescription());

		data.put("sellingPrice", entity.getSellingPrice());

		data.put("displayOrder", entity.getDisplayOrder());

		data.put("available", entity.getAvailable());

		data.put("active", entity.getActive());

		return data;
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}

	private ProductSummaryResponse toSummary(ProductSummaryProjection row) {

		return ProductSummaryResponse.builder()

				.id(row.getId())

				.code(row.getCode())

				.name(row.getName())

				.categoryId(row.getCategoryId())

				.categoryName(row.getCategoryName())

				.sellingPrice(row.getSellingPrice())

				.active(row.getActive())

				.build();
	}

	@Transactional(readOnly = true)
	public PageResponse<ProductSummaryResponse> getProducts(String search, int page, int perPage) {

		PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), perPage, Sort.by("name").ascending());

		Page<ProductSummaryProjection> result = productRepository.findProductSummaries(normalize(search), pageable);

		Page<ProductSummaryResponse> mapped = result.map(this::toSummary);

		return PageResponse.from(mapped);
	}
}
