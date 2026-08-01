package site.ethosylar.pos.common.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.response.AuditActionLookupResponse;
import site.ethosylar.pos.audit.service.AuditActionService;
import site.ethosylar.pos.module.response.ModuleResponse;
import site.ethosylar.pos.module.service.ModuleService;
import site.ethosylar.pos.permission.response.ModulePermissionResponse;
import site.ethosylar.pos.permission.service.ModulePermissionService;
import site.ethosylar.pos.user.response.UserStatusResponse;
import site.ethosylar.pos.user.service.UserStatusService;

@RestController
@RequestMapping("/api/lookups")
@RequiredArgsConstructor
public class LookupController {

	private final UserStatusService userStatusService;

	private final ModuleService moduleService;

	private final ModulePermissionService modulePermissionService;

	private final AuditActionService auditActionService;

	@GetMapping("/user-statuses")
	public ResponseEntity<List<UserStatusResponse>> getUserStatuses() {

		return ResponseEntity.ok(userStatusService.getActiveStatuses());
	}

	@GetMapping("/modules")
	public ResponseEntity<List<ModuleResponse>> getModules() {

		return ResponseEntity.ok(moduleService.getActiveModules());
	}

	@GetMapping("/modules/{moduleId}/permissions")
	public ResponseEntity<List<ModulePermissionResponse>> getModulePermissions(@PathVariable Long moduleId) {

		return ResponseEntity.ok(modulePermissionService.getActivePermissionsByModule(moduleId));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'AUDIT_LOG', " + "'VIEW')")
	@GetMapping("/audit-actions")
	public ResponseEntity<List<AuditActionLookupResponse>> getAuditActions() {

		return ResponseEntity.ok(auditActionService.getActiveActions());
	}
}