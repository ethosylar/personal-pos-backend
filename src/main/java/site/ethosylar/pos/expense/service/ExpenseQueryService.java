package site.ethosylar.pos.expense.service;

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
import site.ethosylar.pos.expense.entity.ExpenseEntity;
import site.ethosylar.pos.expense.mapper.ExpenseMapper;
import site.ethosylar.pos.expense.repository.ExpenseRepository;
import site.ethosylar.pos.expense.request.ExpenseFilterRequest;
import site.ethosylar.pos.expense.response.ExpenseResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseQueryService {

	private final ExpenseRepository expenseRepository;

	private final ExpenseMapper expenseMapper;

	public PageResponse<ExpenseResponse> search(ExpenseFilterRequest filter) {

		PageRequest pageable = PageRequest.of(filter.getPage() - 1, filter.getPerPage(),
				Sort.by(Sort.Direction.DESC, "expenseDate").and(Sort.by(Sort.Direction.DESC, "id")));

		Page<ExpenseResponse> result = expenseRepository.findAll(buildSpecification(filter), pageable)
				.map(expenseMapper::toResponse);

		return PageResponse.from(result);
	}

	private Specification<ExpenseEntity> buildSpecification(ExpenseFilterRequest filter) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			String search = normalize(filter.getSearch());

			if (search != null) {

				String term = "%" + search.toLowerCase(Locale.ROOT) + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.<String>get("expenseNumber")), term),

						cb.like(cb.lower(root.<String>get("description")), term),

						cb.like(cb.lower(root.<String>get("payeeName")), term),

						cb.like(cb.lower(root.<String>get("referenceNo")), term)));
			}

			if (filter.getCategoryId() != null) {

				predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
			}

			if (filter.getSupplierId() != null) {

				predicates.add(cb.equal(root.get("supplier").get("id"), filter.getSupplierId()));
			}

			if (filter.getStatusCode() != null && !filter.getStatusCode().isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("status").get("code")),
						filter.getStatusCode().trim().toLowerCase(Locale.ROOT)));
			}

			if (filter.getFromDate() != null) {

				predicates.add(cb.greaterThanOrEqualTo(root.get("expenseDate"), filter.getFromDate()));
			}

			if (filter.getToDate() != null) {

				predicates.add(cb.lessThanOrEqualTo(root.get("expenseDate"), filter.getToDate()));
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