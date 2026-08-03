package site.ethosylar.pos.expense.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseCategoryResponse {

	private Long id;

	private String code;

	private String name;

	private String description;

	private String expenseClass;
}