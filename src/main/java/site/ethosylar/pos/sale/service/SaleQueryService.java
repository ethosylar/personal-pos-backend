package site.ethosylar.pos.sale.service;

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
import site.ethosylar.pos.sale.entity.SaleEntity;
import site.ethosylar.pos.sale.mapper.SaleMapper;
import site.ethosylar.pos.sale.repository.SaleRepository;
import site.ethosylar.pos.sale.request.SaleFilterRequest;
import site.ethosylar.pos.sale.response.SaleSummaryResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleQueryService {

	private final SaleRepository saleRepository;

	private final SaleMapper saleMapper;

	public PageResponse<SaleSummaryResponse> search(SaleFilterRequest filter) {

		PageRequest pageable = PageRequest.of(filter.getPage() - 1, filter.getPerPage(),
				Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<SaleSummaryResponse> result = saleRepository.findAll(buildSpecification(filter), pageable)
				.map(saleMapper::toSummaryResponse);

		return PageResponse.from(result);
	}

	private Specification<SaleEntity> buildSpecification(SaleFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			String search = normalize(filter.getSearch());

			if (search != null) {

				String term = "%" + search.toLowerCase(Locale.ROOT) + "%";

				predicates.add(cb.like(cb.lower(root.<String>get("saleNumber")), term));
			}

			if (filter.getStatusCode() != null && !filter.getStatusCode().isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("status").get("code")),
						filter.getStatusCode().trim().toLowerCase(Locale.ROOT)));
			}

			if (filter.getCashierUserId() != null) {

				predicates.add(cb.equal(root.get("cashierUser").get("id"), filter.getCashierUserId()));
			}

			if (filter.getFromDate() != null) {

				predicates.add(cb.greaterThanOrEqualTo(root.get("soldAt"), filter.getFromDate()));
			}

			if (filter.getToDate() != null) {

				predicates.add(cb.lessThanOrEqualTo(root.get("soldAt"), filter.getToDate()));
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