package site.ethosylar.pos.staff.controller;

import java.util.List;

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
import site.ethosylar.pos.staff.request.CreateSalaryRateRequest;
import site.ethosylar.pos.staff.request.CreateStaffRequest;
import site.ethosylar.pos.staff.request.StaffFilterRequest;
import site.ethosylar.pos.staff.request.UpdateStaffRequest;
import site.ethosylar.pos.staff.response.SalaryRateResponse;
import site.ethosylar.pos.staff.response.StaffResponse;
import site.ethosylar.pos.staff.response.StaffSummaryResponse;
import site.ethosylar.pos.staff.service.StaffQueryService;
import site.ethosylar.pos.staff.service.StaffSalaryService;
import site.ethosylar.pos.staff.service.StaffService;

@RestController
@RequestMapping(ApiPaths.V1 + "/staff")
@RequiredArgsConstructor
public class StaffController {

	private final StaffService staffService;

	private final StaffQueryService staffQueryService;

	private final StaffSalaryService staffSalaryService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'STAFF', 'VIEW')")
	@GetMapping
	public ResponseEntity<PageResponse<StaffSummaryResponse>> search(@Valid @ModelAttribute StaffFilterRequest filter) {

		return ResponseEntity.ok(staffQueryService.search(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'STAFF', 'VIEW')")
	@GetMapping("/{id}")
	public ResponseEntity<StaffResponse> getById(@PathVariable Long id) {

		return ResponseEntity.ok(staffService.getById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'STAFF', 'CREATE')")
	@PostMapping
	public ResponseEntity<StaffResponse> create(@Valid @RequestBody CreateStaffRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(staffService.create(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'STAFF', 'UPDATE')")
	@PutMapping("/{id}")
	public ResponseEntity<StaffResponse> update(@PathVariable Long id,

			@Valid @RequestBody UpdateStaffRequest request) {

		return ResponseEntity.ok(staffService.update(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'STAFF', 'VIEW')")
	@GetMapping("/{id}/salary-rates")
	public ResponseEntity<List<SalaryRateResponse>> getSalaryHistory(@PathVariable Long id) {

		return ResponseEntity.ok(staffSalaryService.getHistory(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'STAFF', 'MANAGE_SALARY')")
	@PostMapping("/{id}/salary-rates")
	public ResponseEntity<SalaryRateResponse> addSalaryRate(@PathVariable Long id,

			@Valid @RequestBody CreateSalaryRateRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(staffSalaryService.addRate(id, request));
	}
}