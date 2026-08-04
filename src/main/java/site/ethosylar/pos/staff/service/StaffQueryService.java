package site.ethosylar.pos.staff.service;

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
import site.ethosylar.pos.staff.entity.StaffEntity;
import site.ethosylar.pos.staff.mapper.StaffMapper;
import site.ethosylar.pos.staff.repository.StaffRepository;
import site.ethosylar.pos.staff.request.StaffFilterRequest;
import site.ethosylar.pos.staff.response.StaffSummaryResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffQueryService {

	private final StaffRepository staffRepository;

	private final StaffMapper staffMapper;

	public PageResponse<StaffSummaryResponse> search(StaffFilterRequest filter) {

		PageRequest pageable = PageRequest.of(filter.getPage() - 1, filter.getPerPage(),
				Sort.by(Sort.Direction.ASC, "fullName"));

		Page<StaffSummaryResponse> result = staffRepository.findAll(buildSpecification(filter), pageable)
				.map(staffMapper::toSummaryResponse);

		return PageResponse.from(result);
	}

	private Specification<StaffEntity> buildSpecification(StaffFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			String search = normalize(filter.getSearch());

			if (search != null) {

				String term = "%" + search.toLowerCase(Locale.ROOT) + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.<String>get("staffCode")), term),

						cb.like(cb.lower(root.<String>get("fullName")), term),

						cb.like(cb.lower(root.<String>get("email")), term)));
			}

			if (filter.getStatusCode() != null && !filter.getStatusCode().isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("status").get("code")),
						filter.getStatusCode().trim().toLowerCase(Locale.ROOT)));
			}

			if (filter.getUserId() != null) {

				predicates.add(cb.equal(root.get("user").get("id"), filter.getUserId()));
			}

			if (filter.getHiredFrom() != null) {

				predicates.add(cb.greaterThanOrEqualTo(root.get("hireDate"), filter.getHiredFrom()));
			}

			if (filter.getHiredTo() != null) {

				predicates.add(cb.lessThanOrEqualTo(root.get("hireDate"), filter.getHiredTo()));
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
