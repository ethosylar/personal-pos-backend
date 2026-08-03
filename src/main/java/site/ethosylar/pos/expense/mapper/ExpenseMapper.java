package site.ethosylar.pos.expense.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.expense.entity.ExpenseCategoryEntity;
import site.ethosylar.pos.expense.entity.ExpenseEntity;
import site.ethosylar.pos.expense.response.ExpenseCategoryResponse;
import site.ethosylar.pos.expense.response.ExpenseResponse;

@Component
public class ExpenseMapper {

	public ExpenseResponse toResponse(ExpenseEntity expense) {

		return ExpenseResponse.builder().id(expense.getId())

				.expenseNumber(expense.getExpenseNumber())

				.categoryId(expense.getCategory().getId())

				.categoryCode(expense.getCategory().getCode())

				.categoryName(expense.getCategory().getName())

				.expenseClass(expense.getCategory().getExpenseClass())

				.statusId(expense.getStatus().getId())

				.statusCode(expense.getStatus().getCode())

				.statusName(expense.getStatus().getName())

				.supplierId(expense.getSupplier() != null ? expense.getSupplier().getId() : null)

				.supplierCode(expense.getSupplier() != null ? expense.getSupplier().getCode() : null)

				.supplierName(expense.getSupplier() != null ? expense.getSupplier().getName() : null)

				.expenseDate(expense.getExpenseDate())

				.description(expense.getDescription())

				.amount(expense.getAmount())

				.taxAmount(expense.getTaxAmount())

				.totalAmount(expense.getTotalAmount())

				.payeeName(expense.getPayeeName())

				.referenceNo(expense.getReferenceNo())

				.notes(expense.getNotes())

				.createdByUserId(expense.getCreatedByUser() != null ? expense.getCreatedByUser().getId() : null)

				.createdByUsername(expense.getCreatedByUser() != null ? expense.getCreatedByUser().getUsername() : null)

				.postedAt(expense.getPostedAt())

				.postedByUserId(expense.getPostedByUser() != null ? expense.getPostedByUser().getId() : null)

				.postedByUsername(expense.getPostedByUser() != null ? expense.getPostedByUser().getUsername() : null)

				.voidedAt(expense.getVoidedAt())

				.voidedByUserId(expense.getVoidedByUser() != null ? expense.getVoidedByUser().getId() : null)

				.voidedByUsername(expense.getVoidedByUser() != null ? expense.getVoidedByUser().getUsername() : null)

				.voidReason(expense.getVoidReason())

				.createdAt(expense.getCreatedAt())

				.updatedAt(expense.getUpdatedAt())

				.build();
	}

	public ExpenseCategoryResponse toCategoryResponse(ExpenseCategoryEntity category) {

		return ExpenseCategoryResponse.builder().id(category.getId()).code(category.getCode()).name(category.getName())
				.description(category.getDescription()).expenseClass(category.getExpenseClass()).build();
	}
}