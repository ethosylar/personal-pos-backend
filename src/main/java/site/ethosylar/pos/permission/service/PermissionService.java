package site.ethosylar.pos.permission.service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.permission.repository.RoleModulePermissionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

	private final RoleModulePermissionRepository roleModulePermissionRepository;

	public boolean hasPermission(Authentication authentication, String moduleCode, String permissionCode) {

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {

			return false;
		}

		return roleModulePermissionRepository.existsEffectivePermission(authentication.getName(),
				moduleCode.toUpperCase(), permissionCode.toUpperCase());
	}

	public List<String> getEffectivePermissions(String username) {

		if (username == null || username.isBlank()) {

			return Collections.emptyList();
		}

		return roleModulePermissionRepository.findEffectivePermissions(username);
	}
}