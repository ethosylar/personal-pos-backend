package site.ethosylar.pos.expense.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.expense.request.CreateExpenseRequest;
import site.ethosylar.pos.expense.request.ExpenseFilterRequest;
import site.ethosylar.pos.expense.request.UpdateExpenseRequest;
import site.ethosylar.pos.expense.request.VoidExpenseRequest;
import site.ethosylar.pos.expense.response.ExpenseResponse;
import site.ethosylar.pos.expense.service.ExpenseQueryService;
import site.ethosylar.pos.expense.service.ExpenseService;

@RestController
@RequestMapping(ApiPaths.V1 + "/expenses")
@RequiredArgsConstructor
public class ExpenseController {

	private final ExpenseService expenseService;

	private final ExpenseQueryService expenseQueryService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'EXPENSE', 'VIEW')")
	@GetMapping
	public ResponseEntity<PageResponse<ExpenseResponse>> search(@Valid @ModelAttribute ExpenseFilterRequest filter) {

		return ResponseEntity.ok(expenseQueryService.search(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'EXPENSE', 'VIEW')")
	@GetMapping("/{id}")
	public ResponseEntity<ExpenseResponse> getById(@PathVariable Long id) {

		return ResponseEntity.ok(expenseService.getById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'EXPENSE', 'CREATE')")
	@PostMapping
	public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody CreateExpenseRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'EXPENSE', 'UPDATE')")
	@PutMapping("/{id}")
	public ResponseEntity<ExpenseResponse> update(@PathVariable Long id,

			@Valid @RequestBody UpdateExpenseRequest request) {

		return ResponseEntity.ok(expenseService.update(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'EXPENSE', 'POST')")
	@PostMapping("/{id}/post")
	public ResponseEntity<ExpenseResponse> post(@PathVariable Long id) {

		return ResponseEntity.ok(expenseService.post(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'EXPENSE', 'VOID')")
	@PostMapping("/{id}/void")
	public ResponseEntity<ExpenseResponse> voidExpense(@PathVariable Long id,

			@Valid @RequestBody VoidExpenseRequest request) {

		return ResponseEntity.ok(expenseService.voidExpense(id, request));
	}
}