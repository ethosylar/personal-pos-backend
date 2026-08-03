package site.ethosylar.pos.expense.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.expense.entity.ExpenseCategoryEntity;
import site.ethosylar.pos.expense.entity.ExpenseEntity;
import site.ethosylar.pos.expense.entity.ExpenseStatusEntity;
import site.ethosylar.pos.expense.mapper.ExpenseMapper;
import site.ethosylar.pos.expense.repository.ExpenseCategoryRepository;
import site.ethosylar.pos.expense.repository.ExpenseRepository;
import site.ethosylar.pos.expense.repository.ExpenseStatusRepository;
import site.ethosylar.pos.expense.request.CreateExpenseRequest;
import site.ethosylar.pos.expense.request.UpdateExpenseRequest;
import site.ethosylar.pos.expense.request.VoidExpenseRequest;
import site.ethosylar.pos.expense.response.ExpenseResponse;
import site.ethosylar.pos.supplier.entity.SupplierEntity;
import site.ethosylar.pos.supplier.repository.SupplierRepository;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

	private final ExpenseRepository expenseRepository;

	private final ExpenseCategoryRepository categoryRepository;

	private final ExpenseStatusRepository statusRepository;

	private final SupplierRepository supplierRepository;

	private final UserRepository userRepository;

	private final ExpenseNumberService numberService;

	private final ExpenseMapper expenseMapper;

	private final AuditService auditService;

	public ExpenseResponse create(CreateExpenseRequest request) {

		ExpenseCategoryEntity category = getActiveCategory(request.getCategoryId());

		SupplierEntity supplier = resolveSupplier(request.getSupplierId());

		ExpenseEntity expense = new ExpenseEntity();

		expense.setExpenseNumber(numberService.nextExpenseNumber());

		expense.setCategory(category);

		expense.setStatus(getStatus("DRAFT"));

		expense.setSupplier(supplier);

		expense.setExpenseDate(request.getExpenseDate());

		expense.setDescription(request.getDescription().trim());

		expense.setAmount(request.getAmount());

		expense.setTaxAmount(request.getTaxAmount());

		expense.setTotalAmount(calculateTotal(request.getAmount(), request.getTaxAmount()));

		expense.setPayeeName(normalize(request.getPayeeName()));

		expense.setReferenceNo(normalize(request.getReferenceNo()));

		expense.setNotes(normalize(request.getNotes()));

		expense.setCreatedByUser(getCurrentUser());

		expense = expenseRepository.save(expense);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("EXPENSE").entityType("EXPENSE")
				.entityId(expense.getId()).description("Expense created.").afterData(snapshot(expense)).build());

		return expenseMapper.toResponse(expense);
	}

	@Transactional(readOnly = true)
	public ExpenseResponse getById(Long id) {

		ExpenseEntity expense = expenseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Expense not found."));

		return expenseMapper.toResponse(expense);
	}

	public ExpenseResponse update(Long id, UpdateExpenseRequest request) {

		ExpenseEntity expense = getForUpdate(id);

		ensureDraft(expense);

		Map<String, Object> before = snapshot(expense);

		expense.setCategory(getActiveCategory(request.getCategoryId()));

		expense.setSupplier(resolveSupplier(request.getSupplierId()));

		expense.setExpenseDate(request.getExpenseDate());

		expense.setDescription(request.getDescription().trim());

		expense.setAmount(request.getAmount());

		expense.setTaxAmount(request.getTaxAmount());

		expense.setTotalAmount(calculateTotal(request.getAmount(), request.getTaxAmount()));

		expense.setPayeeName(normalize(request.getPayeeName()));

		expense.setReferenceNo(normalize(request.getReferenceNo()));

		expense.setNotes(normalize(request.getNotes()));

		expenseRepository.save(expense);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("EXPENSE").entityType("EXPENSE")
				.entityId(expense.getId()).description("Draft expense updated.").beforeData(before)
				.afterData(snapshot(expense)).build());

		return expenseMapper.toResponse(expense);
	}

	public ExpenseResponse post(Long id) {

		ExpenseEntity expense = getForUpdate(id);

		ensureDraft(expense);

		Map<String, Object> before = snapshot(expense);

		UserEntity currentUser = getCurrentUser();

		expense.setStatus(getStatus("POSTED"));

		expense.setPostedAt(OffsetDateTime.now());

		expense.setPostedByUser(currentUser);

		expenseRepository.save(expense);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("EXPENSE")
				.entityType("EXPENSE").entityId(expense.getId()).description("Expense posted.").beforeData(before)
				.afterData(snapshot(expense)).build());

		return expenseMapper.toResponse(expense);
	}

	public ExpenseResponse voidExpense(Long id, VoidExpenseRequest request) {

		ExpenseEntity expense = getForUpdate(id);

		if (!"POSTED".equalsIgnoreCase(expense.getStatus().getCode())) {

			throw new BusinessRuleException("Only posted expenses can be voided.");
		}

		Map<String, Object> before = snapshot(expense);

		expense.setStatus(getStatus("VOIDED"));

		expense.setVoidedAt(OffsetDateTime.now());

		expense.setVoidedByUser(getCurrentUser());

		expense.setVoidReason(request.getReason().trim());

		expenseRepository.save(expense);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("EXPENSE")
				.entityType("EXPENSE").entityId(expense.getId()).description("Posted expense voided.")
				.beforeData(before).afterData(snapshot(expense)).build());

		return expenseMapper.toResponse(expense);
	}

	private ExpenseEntity getForUpdate(Long id) {

		return expenseRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResourceNotFoundException("Expense not found."));
	}

	private ExpenseStatusEntity getStatus(String code) {

		return statusRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Expense status not found: " + code));
	}

	private ExpenseCategoryEntity getActiveCategory(Long categoryId) {

		ExpenseCategoryEntity category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Expense category not found."));

		if (!Boolean.TRUE.equals(category.getActive())) {

			throw new BusinessRuleException("Expense category is inactive.");
		}

		return category;
	}

	private SupplierEntity resolveSupplier(Long supplierId) {

		if (supplierId == null) {
			return null;
		}

		SupplierEntity supplier = supplierRepository.findById(supplierId)
				.orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

		if (!Boolean.TRUE.equals(supplier.getActive())) {

			throw new BusinessRuleException("Supplier is inactive.");
		}

		return supplier;
	}

	private void ensureDraft(ExpenseEntity expense) {

		if (!"DRAFT".equalsIgnoreCase(expense.getStatus().getCode())) {

			throw new BusinessRuleException("Only draft expenses can be modified.");
		}
	}

	private BigDecimal calculateTotal(BigDecimal amount, BigDecimal taxAmount) {

		return amount.add(taxAmount).setScale(4, RoundingMode.HALF_UP);
	}

	private UserEntity getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}

	private Map<String, Object> snapshot(ExpenseEntity expense) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("expenseNumber", expense.getExpenseNumber());

		data.put("category", expense.getCategory().getCode());

		data.put("status", expense.getStatus().getCode());

		data.put("supplier", expense.getSupplier() != null ? expense.getSupplier().getCode() : null);

		data.put("expenseDate", expense.getExpenseDate());

		data.put("description", expense.getDescription());

		data.put("amount", expense.getAmount());

		data.put("taxAmount", expense.getTaxAmount());

		data.put("totalAmount", expense.getTotalAmount());

		data.put("referenceNo", expense.getReferenceNo());

		data.put("postedAt", expense.getPostedAt());

		data.put("voidedAt", expense.getVoidedAt());

		data.put("voidReason", expense.getVoidReason());

		return data;
	}
}