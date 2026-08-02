package site.ethosylar.pos.auth.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.auth.request.ChangePasswordRequest;
import site.ethosylar.pos.auth.request.LoginRequest;
import site.ethosylar.pos.auth.response.AuthUserResponse;
import site.ethosylar.pos.auth.response.LoginResponse;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.permission.service.PermissionService;
import site.ethosylar.pos.security.jwt.JwtTokenService;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;
import site.ethosylar.pos.user.repository.UserRoleRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final AuthenticationManager authenticationManager;

	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;

	private final PermissionService permissionService;
	private final LoginAttemptService loginAttemptService;
	private final PasswordEncoder passwordEncoder;

	private final JwtTokenService jwtTokenService;

	private final AuditService auditService;

	public LoginResponse login(LoginRequest request) {

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername().trim(), request.getPassword()));

		} catch (org.springframework.security.authentication.BadCredentialsException ex) {

			loginAttemptService.recordFailure(request.getUsername());

			throw ex;
		}

		UserEntity user = getUserByUsername(request.getUsername());

		loginAttemptService.resetFailures(user);

		user.setLastLoginAt(OffsetDateTime.now());

		user.setFailedLoginAttempts(0);

		userRepository.save(user);

		Map<String, Object> loginAudit = new LinkedHashMap<>();

		loginAudit.put("username", user.getUsername());

		loginAudit.put("status", user.getStatus().getCode());

		auditService.log(AuditLogRequest.builder().actorUserId(user.getId()).actionCode("LOGIN_SUCCESS")
				.entityType("AUTHENTICATION").entityId(user.getId()).description("User successfully logged in.")
				.afterData(loginAudit).build());

		String token = jwtTokenService.generateAccessToken(user);

		return LoginResponse.builder().tokenType("Bearer").accessToken(token)
				.expiresIn(jwtTokenService.getExpiresInSeconds()).user(buildAuthUserResponse(user)).build();
	}

	@Transactional(readOnly = true)
	public AuthUserResponse getCurrentUser(String username) {

		return buildAuthUserResponse(getUserByUsername(username));
	}

	private AuthUserResponse buildAuthUserResponse(UserEntity user) {

		List<String> roles = userRoleRepository.findByUser_Id(user.getId()).stream()
				.filter(userRole -> Boolean.TRUE.equals(userRole.getRole().getActive()))
				.map(userRole -> userRole.getRole().getCode()).distinct().sorted().toList();

		List<String> effectivePermissions = permissionService.getEffectivePermissions(user.getUsername());

		Map<String, List<String>> groupedPermissions = new LinkedHashMap<>();

		for (String value : effectivePermissions) {

			String[] parts = value.split(":", 2);

			if (parts.length != 2) {
				continue;
			}

			groupedPermissions.computeIfAbsent(parts[0], key -> new ArrayList<>()).add(parts[1]);
		}

		return AuthUserResponse.builder().id(user.getId()).username(user.getUsername()).fullName(user.getFullName())
				.email(user.getEmail()).status(user.getStatus().getCode()).roles(roles).permissions(groupedPermissions)
				.build();
	}

	private UserEntity getUserByUsername(String username) {

		return userRepository.findByUsernameIgnoreCase(username.trim())
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}

	public void changePassword(String username, ChangePasswordRequest request) {

		UserEntity user = getUserByUsername(username);

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {

			throw new BusinessRuleException("Current password is incorrect.");
		}

		if (!request.getNewPassword().equals(request.getConfirmPassword())) {

			throw new BusinessRuleException("New password and confirmation do not match.");
		}

		if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {

			throw new BusinessRuleException("New password must be different from the current password.");
		}

		user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

		user.setPasswordChangedAt(OffsetDateTime.now());

		userRepository.save(user);

		Map<String, Object> auditData = new LinkedHashMap<>();

		auditData.put("passwordChanged", true);

		auditData.put("changedAt", user.getPasswordChangedAt());

		auditService.log(AuditLogRequest.builder().actorUserId(user.getId()).actionCode("PASSWORD_CHANGE")
				.moduleCode("USER_MANAGEMENT").entityType("USER").entityId(user.getId())
				.description("User changed their password.").afterData(auditData).build());
	}
}