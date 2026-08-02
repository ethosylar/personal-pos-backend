package site.ethosylar.pos.audit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.entity.AuditLogEntity;
import site.ethosylar.pos.audit.mapper.AuditLogMapper;
import site.ethosylar.pos.audit.repository.AuditLogRepository;
import site.ethosylar.pos.audit.request.AuditLogFilterRequest;
import site.ethosylar.pos.audit.response.AuditLogResponse;
import site.ethosylar.pos.common.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogQueryService {

	private final AuditLogRepository auditLogRepository;

	private final AuditLogMapper auditLogMapper;

	public PageResponse<AuditLogResponse> search(AuditLogFilterRequest filter) {

		int pageIndex = Math.max(filter.getPage() - 1, 0);

		PageRequest pageable = PageRequest.of(pageIndex, filter.getPerPage(),
				Sort.by(Sort.Direction.DESC, "createdAt"));

		Specification<AuditLogEntity> specification = buildSpecification(filter);

		Page<AuditLogEntity> page = auditLogRepository.findAll(specification, pageable);

		Page<AuditLogResponse> mapped = page.map(auditLogMapper::toResponse);

		return PageResponse.from(mapped);
	}

	private Specification<AuditLogEntity> buildSpecification(AuditLogFilterRequest filter) {

		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			/*
			 * User
			 */
			if (filter.getUserId() != null) {

				predicates.add(criteriaBuilder.equal(root.get("user").get("id"), filter.getUserId()));
			}

			/*
			 * Audit action
			 */
			String actionCode = normalize(filter.getActionCode());

			if (actionCode != null) {

				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("action").get("code")),
						actionCode.toLowerCase(Locale.ROOT)));
			}

			/*
			 * Module
			 */
			String moduleCode = normalize(filter.getModuleCode());

			if (moduleCode != null) {

				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("module").get("code")),
						moduleCode.toLowerCase(Locale.ROOT)));
			}

			/*
			 * Entity type
			 */
			String entityType = normalize(filter.getEntityType());

			if (entityType != null) {

				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("entityType")),
						entityType.toLowerCase(Locale.ROOT)));
			}

			/*
			 * Entity ID
			 */
			if (filter.getEntityId() != null) {

				predicates.add(criteriaBuilder.equal(root.get("entityId"), filter.getEntityId()));
			}

			/*
			 * From date
			 */
			if (filter.getFromDate() != null) {

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filter.getFromDate()));
			}

			/*
			 * To date
			 */
			if (filter.getToDate() != null) {

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filter.getToDate()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}
}