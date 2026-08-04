package site.ethosylar.pos.payroll.service;

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
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.payroll.entity.StaffMonthlyCostEntity;
import site.ethosylar.pos.payroll.mapper.PayrollMapper;
import site.ethosylar.pos.payroll.repository.StaffMonthlyCostRepository;
import site.ethosylar.pos.payroll.request.StaffMonthlyCostFilterRequest;
import site.ethosylar.pos.payroll.response.StaffMonthlyCostResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollQueryService {

	private final StaffMonthlyCostRepository costRepository;

	private final PayrollMapper payrollMapper;

	public PageResponse<StaffMonthlyCostResponse> search(StaffMonthlyCostFilterRequest filter) {

		PageRequest pageable = PageRequest.of(filter.getPage() - 1, filter.getPerPage(),

				Sort.by(Sort.Direction.DESC, "periodYear").and(Sort.by(Sort.Direction.DESC, "periodMonth"))
						.and(Sort.by(Sort.Direction.DESC, "id")));

		Page<StaffMonthlyCostResponse> result = costRepository.findAll(buildSpecification(filter), pageable)
				.map(payrollMapper::toResponse);

		return PageResponse.from(result);
	}

	private Specification<StaffMonthlyCostEntity> buildSpecification(StaffMonthlyCostFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			String search = normalize(filter.getSearch());

			if (search != null) {

				String term = "%" + search.toLowerCase(Locale.ROOT) + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.<String>get("costNumber")), term),

						cb.like(cb.lower(root.get("staff").<String>get("staffCode")), term),

						cb.like(cb.lower(root.get("staff").<String>get("fullName")), term)));
			}

			if (filter.getStaffId() != null) {

				predicates.add(cb.equal(root.get("staff").get("id"), filter.getStaffId()));
			}

			if (filter.getStatusCode() != null && !filter.getStatusCode().isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("status").get("code")),
						filter.getStatusCode().trim().toLowerCase(Locale.ROOT)));
			}

			if (filter.getPeriodYear() != null) {

				predicates.add(cb.equal(root.get("periodYear"), filter.getPeriodYear().shortValue()));
			}

			if (filter.getPeriodMonth() != null) {

				predicates.add(cb.equal(root.get("periodMonth"), filter.getPeriodMonth().shortValue()));
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