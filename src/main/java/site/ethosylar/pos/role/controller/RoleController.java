package site.ethosylar.pos.role.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.role.request.AssignModulePermissionsRequest;
import site.ethosylar.pos.role.request.AssignRoleModulesRequest;
import site.ethosylar.pos.role.request.CreateRoleRequest;
import site.ethosylar.pos.role.request.UpdateRoleRequest;
import site.ethosylar.pos.role.response.RoleDetailResponse;
import site.ethosylar.pos.role.response.RoleResponse;
import site.ethosylar.pos.role.service.RoleService;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

	private final RoleService roleService;

	@GetMapping
	public ResponseEntity<List<RoleResponse>> getAllRoles() {

		return ResponseEntity.ok(roleService.getAllRoles());
	}

	@GetMapping("/{id}")
	public ResponseEntity<RoleDetailResponse> getRoleById(@PathVariable Long id) {

		return ResponseEntity.ok(roleService.getRoleById(id));
	}

	@PostMapping
	public ResponseEntity<RoleDetailResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<RoleDetailResponse> updateRole(@PathVariable Long id,
			@Valid @RequestBody UpdateRoleRequest request) {

		return ResponseEntity.ok(roleService.updateRole(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRole(@PathVariable Long id) {

		roleService.deleteRole(id);

		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/modules")
	public ResponseEntity<RoleDetailResponse> assignModules(@PathVariable Long id,
			@Valid @RequestBody AssignRoleModulesRequest request) {

		return ResponseEntity.ok(roleService.assignModules(id, request));
	}

	@PutMapping("/{roleId}/modules/{moduleId}/permissions")
	public ResponseEntity<RoleDetailResponse> assignModulePermissions(@PathVariable Long roleId,
			@PathVariable Long moduleId, @Valid @RequestBody AssignModulePermissionsRequest request) {

		return ResponseEntity.ok(roleService.assignModulePermissions(roleId, moduleId, request));
	}
}