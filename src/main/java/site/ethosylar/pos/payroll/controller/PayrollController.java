package site.ethosylar.pos.payroll.controller;

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
import site.ethosylar.pos.payroll.request.CreateStaffMonthlyCostRequest;
import site.ethosylar.pos.payroll.request.StaffMonthlyCostFilterRequest;
import site.ethosylar.pos.payroll.request.UpdateStaffMonthlyCostRequest;
import site.ethosylar.pos.payroll.request.VoidStaffMonthlyCostRequest;
import site.ethosylar.pos.payroll.response.StaffMonthlyCostResponse;
import site.ethosylar.pos.payroll.service.PayrollQueryService;
import site.ethosylar.pos.payroll.service.StaffMonthlyCostService;

@RestController
@RequestMapping(ApiPaths.V1 + "/payroll")
@RequiredArgsConstructor
public class PayrollController {

	private final StaffMonthlyCostService payrollService;

	private final PayrollQueryService payrollQueryService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PAYROLL', 'VIEW')")
	@GetMapping
	public ResponseEntity<PageResponse<StaffMonthlyCostResponse>> search(
			@Valid @ModelAttribute StaffMonthlyCostFilterRequest filter) {

		return ResponseEntity.ok(payrollQueryService.search(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PAYROLL', 'VIEW')")
	@GetMapping("/{id}")
	public ResponseEntity<StaffMonthlyCostResponse> getById(@PathVariable Long id) {

		return ResponseEntity.ok(payrollService.getById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PAYROLL', 'CREATE')")
	@PostMapping
	public ResponseEntity<StaffMonthlyCostResponse> create(@Valid @RequestBody CreateStaffMonthlyCostRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.create(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PAYROLL', 'UPDATE')")
	@PutMapping("/{id}")
	public ResponseEntity<StaffMonthlyCostResponse> update(@PathVariable Long id,

			@Valid @RequestBody UpdateStaffMonthlyCostRequest request) {

		return ResponseEntity.ok(payrollService.update(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PAYROLL', 'POST')")
	@PostMapping("/{id}/post")
	public ResponseEntity<StaffMonthlyCostResponse> post(@PathVariable Long id) {

		return ResponseEntity.ok(payrollService.post(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PAYROLL', 'VOID')")
	@PostMapping("/{id}/void")
	public ResponseEntity<StaffMonthlyCostResponse> voidCost(@PathVariable Long id,

			@Valid @RequestBody VoidStaffMonthlyCostRequest request) {

		return ResponseEntity.ok(payrollService.voidCost(id, request));
	}
}