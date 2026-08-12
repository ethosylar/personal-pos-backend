package site.ethosylar.pos.supplier.service;

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
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.common.request.ChangeActiveStatusRequest;
import site.ethosylar.pos.common.request.MasterDataFilterRequest;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.supplier.entity.SupplierEntity;
import site.ethosylar.pos.supplier.mapper.SupplierMapper;
import site.ethosylar.pos.supplier.projection.SupplierSummaryProjection;
import site.ethosylar.pos.supplier.repository.SupplierRepository;
import site.ethosylar.pos.supplier.request.CreateSupplierRequest;
import site.ethosylar.pos.supplier.request.UpdateSupplierRequest;
import site.ethosylar.pos.supplier.response.SupplierResponse;
import site.ethosylar.pos.supplier.response.SupplierSummaryResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {

	private final SupplierRepository repository;
	private final SupplierMapper mapper;
	private final AuditService auditService;
	@Transactional(readOnly = true)
	public PageResponse<SupplierResponse> search(MasterDataFilterRequest filter) {

		PageRequest pageable = PageRequest.of(filter.getPage() - 1, filter.getPerPage(),
				Sort.by(Sort.Order.asc("contactPerson"), Sort.Order.asc("name")));

		Specification<SupplierEntity> specification = buildSpecification(filter);

		Page<SupplierResponse> page = repository.findAll(specification, pageable).map(mapper::toResponse);

		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public SupplierResponse getById(Long id) {

		return mapper.toResponse(getEntity(id));
	}

	public SupplierResponse create(CreateSupplierRequest request) {

		String code = request.getCode().trim().toUpperCase(Locale.ROOT);

		if (repository.existsByCodeIgnoreCase(code)) {

			throw new DuplicateResourceException("Supplier code already exists.");
		}

		SupplierEntity entity = new SupplierEntity();

		entity.setCode(code);
		entity.setName(request.getName().trim());
		entity.setContactPerson(request.getContactPerson());
		entity.setPhone(request.getPhone());
		entity.setEmail(request.getEmail());
		entity.setAddressLine1(request.getAddressLine1());
		entity.setAddressLine2(request.getAddressLine2());
		entity.setCity(request.getCity());
		entity.setState(request.getState());
		entity.setPostcode(request.getPostcode());
		entity.setCountry(request.getCountry());
		entity.setRegistrationNo(request.getRegistrationNo());
		entity.setTaxRegistrationNo(request.getTaxRegistrationNo());
		entity.setPaymentTermsDays(request.getPaymentTermsDays());
		entity.setNotes(request.getNotes());
		entity.setActive(true);

		entity = repository.save(entity);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("SUPPLIER").entityType("SUPPLIER")
				.entityId(entity.getId()).description("Supplier created.").afterData(snapshot(entity)).build());

		return mapper.toResponse(entity);
	}

	public SupplierResponse update(Long id, UpdateSupplierRequest request) {

		SupplierEntity entity = getEntity(id);

		Map<String, Object> before = snapshot(entity);

		entity.setName(request.getName().trim());
		entity.setContactPerson(request.getContactPerson());
		entity.setPhone(request.getPhone());
		entity.setEmail(request.getEmail());
		entity.setAddressLine1(request.getAddressLine1());
		entity.setAddressLine2(request.getAddressLine2());
		entity.setCity(request.getCity());
		entity.setState(request.getState());
		entity.setPostcode(request.getPostcode());
		entity.setCountry(request.getCountry());
		entity.setRegistrationNo(request.getRegistrationNo());
		entity.setTaxRegistrationNo(request.getTaxRegistrationNo());
		entity.setPaymentTermsDays(request.getPaymentTermsDays());
		entity.setNotes(request.getNotes());

		repository.save(entity);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("SUPPLIER").entityType("SUPPLIER")
				.entityId(entity.getId()).description("Supplier updated.").beforeData(before)
				.afterData(snapshot(entity)).build());

		return mapper.toResponse(entity);
	}

	public SupplierResponse changeStatus(Long id, ChangeActiveStatusRequest request) {

		SupplierEntity entity = getEntity(id);

		Map<String, Object> before = snapshot(entity);

		entity.setActive(request.getActive());

		repository.save(entity);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("SUPPLIER")
				.entityType("SUPPLIER").entityId(entity.getId()).description("Supplier status changed.")
				.beforeData(before).afterData(snapshot(entity)).build());

		return mapper.toResponse(entity);
	}

	private SupplierEntity getEntity(Long id) {

		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));
	}

	private Specification<SupplierEntity> buildSpecification(MasterDataFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			String search = normalize(filter.getSearch());

			if (search != null) {

				String term = "%" + search.toLowerCase(Locale.ROOT) + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.<String>get("code")), term),
						cb.like(cb.lower(root.<String>get("name")), term),
						cb.like(cb.lower(root.<String>get("contactPerson")), term),
						cb.like(cb.lower(root.<String>get("email")), term)));
			}

			if (filter.getActive() != null) {

				predicates.add(cb.equal(root.get("active"), filter.getActive()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Map<String, Object> snapshot(SupplierEntity entity) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("code", entity.getCode());
		data.put("name", entity.getName());
		data.put("contactPerson", entity.getContactPerson());
		data.put("phone", entity.getPhone());
		data.put("email", entity.getEmail());
		data.put("addressLine1", entity.getAddressLine1());
		data.put("addressLine2", entity.getAddressLine2());
		data.put("city", entity.getCity());
		data.put("state", entity.getState());
		data.put("postcode", entity.getPostcode());
		data.put("country", entity.getCountry());
		data.put("registrationNo", entity.getRegistrationNo());
		data.put("taxRegistrationNo", entity.getTaxRegistrationNo());
		data.put("paymentTermsDays", entity.getPaymentTermsDays());
		data.put("notes", entity.getNotes());
		data.put("active", entity.getActive());

		return data;
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}

	private SupplierSummaryResponse toSummary(SupplierSummaryProjection row) {

		return SupplierSummaryResponse.builder()

				.id(row.getId())

				.code(row.getCode())

				.name(row.getName())

				.contactPerson(row.getContactPerson())

				.phone(row.getPhone())

				.email(row.getEmail())

				.active(row.getActive())

				.build();
	}

	@Transactional(readOnly = true)
	public PageResponse<SupplierSummaryResponse> getSuppliers(String search, int page, int perPage) {

		int safePage = Math.max(page, 1);

		int safePerPage = Math.min(Math.max(perPage, 1), 100);

		PageRequest pageable = PageRequest.of(safePage - 1, safePerPage, Sort.by("name").ascending());

		Page<SupplierSummaryProjection> result = repository.findSupplierSummaries(normalize(search), pageable);

		Page<SupplierSummaryResponse> mapped = result.map(this::toSummary);

		return PageResponse.from(mapped);
	}
}