package site.ethosylar.pos.role.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.module.entity.ModuleEntity;
import site.ethosylar.pos.module.repository.ModuleRepository;
import site.ethosylar.pos.permission.entity.ModulePermissionEntity;
import site.ethosylar.pos.permission.entity.RoleModulePermissionEntity;
import site.ethosylar.pos.permission.repository.ModulePermissionRepository;
import site.ethosylar.pos.permission.repository.RoleModulePermissionRepository;
import site.ethosylar.pos.role.entity.RoleEntity;
import site.ethosylar.pos.role.entity.RoleModuleEntity;
import site.ethosylar.pos.role.mapper.RoleMapper;
import site.ethosylar.pos.role.repository.RoleModuleRepository;
import site.ethosylar.pos.role.repository.RoleRepository;
import site.ethosylar.pos.role.request.AssignModulePermissionsRequest;
import site.ethosylar.pos.role.request.AssignRoleModulesRequest;
import site.ethosylar.pos.role.request.CreateRoleRequest;
import site.ethosylar.pos.role.request.UpdateRoleRequest;
import site.ethosylar.pos.role.response.RoleDetailResponse;
import site.ethosylar.pos.role.response.RoleResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

	private final RoleRepository roleRepository;
	private final RoleModuleRepository roleModuleRepository;

	private final ModuleRepository moduleRepository;

	private final ModulePermissionRepository modulePermissionRepository;

	private final RoleModulePermissionRepository roleModulePermissionRepository;

	private final RoleMapper roleMapper;

	private final AuditService auditService;

	@Transactional(readOnly = true)
	public List<RoleResponse> getAllRoles() {

		return roleRepository.findAll().stream().map(roleMapper::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public RoleDetailResponse getRoleById(Long id) {

		RoleEntity role = getRoleEntity(id);

		return buildDetailResponse(role);
	}

	public RoleDetailResponse createRole(CreateRoleRequest request) {

		String code = request.getCode().trim().toUpperCase();

		if (roleRepository.existsByCodeIgnoreCase(code)) {

			throw new DuplicateResourceException("Role code already exists.");
		}

		RoleEntity role = new RoleEntity();

		role.setCode(code);
		role.setName(request.getName().trim());
		role.setDescription(request.getDescription());
		role.setSystem(false);
		role.setActive(true);

		role = roleRepository.save(role);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("ROLE_PERMISSION_MANAGEMENT")
				.entityType("ROLE").entityId(role.getId()).description("Role created.")
				.afterData(roleAuditSnapshot(role)).build());

		return buildDetailResponse(role);
	}

	public RoleDetailResponse updateRole(Long id, UpdateRoleRequest request) {

		RoleEntity role = getRoleEntity(id);

		Map<String, Object> before = roleAuditSnapshot(role);

		if (Boolean.FALSE.equals(request.getActive()) && Boolean.TRUE.equals(role.getSystem())) {

			throw new BusinessRuleException("System roles cannot be deactivated.");
		}

		role.setName(request.getName().trim());
		role.setDescription(request.getDescription());

		if (request.getActive() != null) {
			role.setActive(request.getActive());
		}

		roleRepository.save(role);

		Map<String, Object> after = roleAuditSnapshot(role);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("ROLE_PERMISSION_MANAGEMENT")
				.entityType("ROLE").entityId(role.getId()).description("Role updated.").beforeData(before)
				.afterData(after).build());

		return buildDetailResponse(role);
	}

	public void deleteRole(Long id) {

		RoleEntity role = getRoleEntity(id);

		Map<String, Object> before = roleAuditSnapshot(role);

		if (Boolean.TRUE.equals(role.getSystem())) {

			throw new BusinessRuleException("System roles cannot be deleted.");
		}

		roleRepository.delete(role);

		auditService.log(AuditLogRequest.builder().actionCode("DELETE").moduleCode("ROLE_PERMISSION_MANAGEMENT")
				.entityType("ROLE").entityId(id).description("Role deleted.").beforeData(before).build());
	}

	public RoleDetailResponse assignModules(Long roleId, AssignRoleModulesRequest request) {

		RoleEntity role = getRoleEntity(roleId);

		List<String> modulesBefore = getAssignedModuleCodes(roleId);

		List<Long> moduleIds = request.getModuleIds().stream().distinct().toList();

		List<ModuleEntity> modules = moduleRepository.findAllById(moduleIds);

		if (modules.size() != moduleIds.size()) {

			throw new ResourceNotFoundException("One or more selected modules do not exist.");
		}

		List<RoleModuleEntity> existing = roleModuleRepository.findByRole_Id(roleId);

		List<RoleModuleEntity> toDelete = existing.stream()
				.filter(roleModule -> !moduleIds.contains(roleModule.getModule().getId())).toList();

		roleModuleRepository.deleteAll(toDelete);

		for (ModuleEntity module : modules) {

			boolean alreadyAssigned = existing.stream()
					.anyMatch(roleModule -> roleModule.getModule().getId().equals(module.getId()));

			if (!alreadyAssigned) {

				RoleModuleEntity roleModule = new RoleModuleEntity();

				roleModule.setRole(role);
				roleModule.setModule(module);
				roleModule.setAssignedBy(null);

				roleModuleRepository.save(roleModule);
			}
		}

		List<String> modulesAfter = getAssignedModuleCodes(roleId);

		Map<String, Object> before = new LinkedHashMap<>();

		before.put("modules", modulesBefore);

		Map<String, Object> after = new LinkedHashMap<>();

		after.put("modules", modulesAfter);

		auditService.log(AuditLogRequest.builder().actionCode("MODULE_ASSIGNMENT")
				.moduleCode("ROLE_PERMISSION_MANAGEMENT").entityType("ROLE").entityId(roleId)
				.description("Role module assignments changed.").beforeData(before).afterData(after).build());

		return buildDetailResponse(role);
	}

	public RoleDetailResponse assignModulePermissions(Long roleId, Long moduleId,
			AssignModulePermissionsRequest request) {

		RoleEntity role = getRoleEntity(roleId);

		RoleModuleEntity roleModule = roleModuleRepository.findByRole_IdAndModule_Id(roleId, moduleId)
				.orElseThrow(() -> new BusinessRuleException("The selected module is not assigned to this role."));

		List<String> permissionsBefore = getAssignedPermissionCodes(roleModule.getId());

		List<Long> permissionIds = request.getPermissionIds().stream().distinct().toList();

		List<ModulePermissionEntity> permissions = modulePermissionRepository.findAllById(permissionIds);

		if (permissions.size() != permissionIds.size()) {

			throw new ResourceNotFoundException("One or more selected permissions do not exist.");
		}

		boolean invalidPermission = permissions.stream()
				.anyMatch(permission -> !permission.getModule().getId().equals(moduleId));

		if (invalidPermission) {

			throw new BusinessRuleException("One or more permissions do not belong to the selected module.");
		}

		List<RoleModulePermissionEntity> existing = roleModulePermissionRepository
				.findByRoleModule_Id(roleModule.getId());

		roleModulePermissionRepository.deleteAll(existing);

		for (ModulePermissionEntity permission : permissions) {

			RoleModulePermissionEntity assignment = new RoleModulePermissionEntity();

			assignment.setRoleModule(roleModule);
			assignment.setModulePermission(permission);
			assignment.setGrantedBy(null);

			roleModulePermissionRepository.save(assignment);
		}

		List<String> permissionsAfter = getAssignedPermissionCodes(roleModule.getId());

		Map<String, Object> before = new LinkedHashMap<>();

		before.put("module", roleModule.getModule().getCode());

		before.put("permissions", permissionsBefore);

		Map<String, Object> after = new LinkedHashMap<>();

		after.put("module", roleModule.getModule().getCode());

		after.put("permissions", permissionsAfter);

		auditService.log(AuditLogRequest.builder().actionCode("PERMISSION_ASSIGNMENT")
				.moduleCode("ROLE_PERMISSION_MANAGEMENT").entityType("ROLE").entityId(roleId)
				.description("Role module permissions changed.").beforeData(before).afterData(after).build());

		return buildDetailResponse(role);
	}

	private RoleDetailResponse buildDetailResponse(RoleEntity role) {

		List<RoleModuleEntity> roleModules = roleModuleRepository.findByRole_Id(role.getId());

		Map<Long, List<RoleModulePermissionEntity>> permissionsByRoleModule = new HashMap<>();

		for (RoleModuleEntity roleModule : roleModules) {

			permissionsByRoleModule.put(roleModule.getId(),
					roleModulePermissionRepository.findByRoleModule_Id(roleModule.getId()));
		}

		return roleMapper.toDetailResponse(role, roleModules, permissionsByRoleModule);
	}

	private RoleEntity getRoleEntity(Long id) {

		return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found."));
	}

	private Map<String, Object> roleAuditSnapshot(RoleEntity role) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("id", role.getId());

		data.put("code", role.getCode());

		data.put("name", role.getName());

		data.put("description", role.getDescription());

		data.put("system", role.getSystem());

		data.put("active", role.getActive());

		return data;
	}

	private List<String> getAssignedModuleCodes(Long roleId) {

		return roleModuleRepository.findByRole_Id(roleId).stream().map(roleModule -> roleModule.getModule().getCode())
				.sorted().toList();
	}

	private List<String> getAssignedPermissionCodes(Long roleModuleId) {

		return roleModulePermissionRepository.findByRoleModule_Id(roleModuleId).stream()
				.map(assignment -> assignment.getModulePermission().getCode()).sorted().toList();
	}
}