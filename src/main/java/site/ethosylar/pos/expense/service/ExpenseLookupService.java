package site.ethosylar.pos.expense.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.expense.mapper.ExpenseMapper;
import site.ethosylar.pos.expense.repository.ExpenseCategoryRepository;
import site.ethosylar.pos.expense.response.ExpenseCategoryResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseLookupService {

	private final ExpenseCategoryRepository categoryRepository;

	private final ExpenseMapper expenseMapper;

	public List<ExpenseCategoryResponse> getActiveCategories() {

		return categoryRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc().stream()
				.map(expenseMapper::toCategoryResponse).toList();
	}
}