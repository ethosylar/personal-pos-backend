package site.ethosylar.pos.audit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogFilterRequest;
import site.ethosylar.pos.audit.response.AuditLogResponse;
import site.ethosylar.pos.audit.service.AuditLogQueryService;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.common.response.PageResponse;

@RestController
@RequestMapping(ApiPaths.V1 + "/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

	private final AuditLogQueryService auditLogQueryService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'AUDIT_LOG', " + "'VIEW')")
	@GetMapping
	public ResponseEntity<PageResponse<AuditLogResponse>> search(@Valid @ModelAttribute AuditLogFilterRequest filter) {

		return ResponseEntity.ok(auditLogQueryService.search(filter));
	}
}