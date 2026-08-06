package site.ethosylar.pos.accounting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.accounting.request.GenerateMonthlyAccountingRequest;
import site.ethosylar.pos.accounting.request.MonthlyAccountingFilterRequest;
import site.ethosylar.pos.accounting.request.VoidMonthlyAccountingRequest;
import site.ethosylar.pos.accounting.response.MonthlyAccountingResponse;
import site.ethosylar.pos.accounting.service.MonthlyAccountingQueryService;
import site.ethosylar.pos.accounting.service.MonthlyAccountingService;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.common.response.PageResponse;

@RestController
@RequestMapping(ApiPaths.V1 + "/accounting/monthly")
@RequiredArgsConstructor
public class MonthlyAccountingController {

	private final MonthlyAccountingService accountingService;

	private final MonthlyAccountingQueryService queryService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'ACCOUNTING', 'VIEW')")
	@GetMapping
	public ResponseEntity<PageResponse<MonthlyAccountingResponse>> search(
			@Valid @ModelAttribute MonthlyAccountingFilterRequest filter) {

		return ResponseEntity.ok(queryService.search(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'ACCOUNTING', 'VIEW')")
	@GetMapping("/{id}")
	public ResponseEntity<MonthlyAccountingResponse> getById(@PathVariable Long id) {

		return ResponseEntity.ok(accountingService.getById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'ACCOUNTING', 'GENERATE')")
	@PostMapping
	public ResponseEntity<MonthlyAccountingResponse> generate(
			@Valid @RequestBody GenerateMonthlyAccountingRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(accountingService.generate(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'ACCOUNTING', 'REFRESH')")
	@PostMapping("/{id}/refresh")
	public ResponseEntity<MonthlyAccountingResponse> refresh(@PathVariable Long id) {

		return ResponseEntity.ok(accountingService.refresh(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'ACCOUNTING', 'POST')")
	@PostMapping("/{id}/post")
	public ResponseEntity<MonthlyAccountingResponse> post(@PathVariable Long id) {

		return ResponseEntity.ok(accountingService.post(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'ACCOUNTING', 'VOID')")
	@PostMapping("/{id}/void")
	public ResponseEntity<MonthlyAccountingResponse> voidAccounting(@PathVariable Long id,

			@Valid @RequestBody VoidMonthlyAccountingRequest request) {

		return ResponseEntity.ok(accountingService.voidAccounting(id, request));
	}
}
