package site.ethosylar.pos.auth.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.entity.UserStatusEntity;
import site.ethosylar.pos.user.repository.UserRepository;
import site.ethosylar.pos.user.repository.UserStatusRepository;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final AuditService auditService;

	@Value("${pos.security.login.max-failed-attempts:5}")
	private int maxFailedAttempts;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void recordFailure(String username) {

		userRepository.findByUsernameIgnoreCase(username.trim()).ifPresentOrElse(user -> {

			int attempts = user.getFailedLoginAttempts() == null ? 1 : user.getFailedLoginAttempts() + 1;

			user.setFailedLoginAttempts(attempts);

			boolean locked = false;

			if (attempts >= maxFailedAttempts) {

				UserStatusEntity lockedStatus = userStatusRepository.findByCodeIgnoreCase("LOCKED").orElseThrow();

				user.setStatus(lockedStatus);

				locked = true;
			}

			userRepository.save(user);

			Map<String, Object> auditData = new LinkedHashMap<>();

			auditData.put("username", user.getUsername());

			auditData.put("failedAttempts", attempts);

			auditData.put("accountLocked", locked);

			auditService.log(AuditLogRequest.builder().actorUserId(user.getId()).actionCode("LOGIN_FAILURE")
					.entityType("AUTHENTICATION").entityId(user.getId()).description("Failed login attempt.")
					.afterData(auditData).build());

		}, () -> {
			Map<String, Object> auditData = new LinkedHashMap<>();

			auditData.put("username", username.trim());

			auditData.put("userExists", false);

			auditService.log(AuditLogRequest.builder().actionCode("LOGIN_FAILURE").entityType("AUTHENTICATION")
					.description("Failed login attempt for an unknown username.").afterData(auditData).build());
		});
	}

	@Transactional
	public void resetFailures(UserEntity user) {

		if (user.getFailedLoginAttempts() == null || user.getFailedLoginAttempts() != 0) {

			user.setFailedLoginAttempts(0);

			userRepository.save(user);
		}
	}
}