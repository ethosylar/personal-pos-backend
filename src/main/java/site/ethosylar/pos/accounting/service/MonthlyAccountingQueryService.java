package site.ethosylar.pos.accounting.service;

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
import site.ethosylar.pos.accounting.entity.MonthlyAccountingEntity;
import site.ethosylar.pos.accounting.mapper.MonthlyAccountingMapper;
import site.ethosylar.pos.accounting.repository.MonthlyAccountingRepository;
import site.ethosylar.pos.accounting.request.MonthlyAccountingFilterRequest;
import site.ethosylar.pos.accounting.response.MonthlyAccountingResponse;
import site.ethosylar.pos.common.response.PageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyAccountingQueryService {

	private final MonthlyAccountingRepository accountingRepository;

	private final MonthlyAccountingMapper mapper;

	public PageResponse<MonthlyAccountingResponse> search(MonthlyAccountingFilterRequest filter) {

		PageRequest pageable = PageRequest.of(filter.getPage() - 1, filter.getPerPage(),
				Sort.by(Sort.Direction.DESC, "periodYear").and(Sort.by(Sort.Direction.DESC, "periodMonth"))
						.and(Sort.by(Sort.Direction.DESC, "id")));

		Page<MonthlyAccountingResponse> result = accountingRepository.findAll(buildSpecification(filter), pageable)
				.map(mapper::toResponse);

		return PageResponse.from(result);
	}

	private Specification<MonthlyAccountingEntity> buildSpecification(MonthlyAccountingFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

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

}
