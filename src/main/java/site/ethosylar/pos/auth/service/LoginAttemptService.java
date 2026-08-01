package site.ethosylar.pos.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.entity.UserStatusEntity;
import site.ethosylar.pos.user.repository.UserRepository;
import site.ethosylar.pos.user.repository.UserStatusRepository;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Value("${pos.security.login.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(String username) {

        userRepository
                .findByUsernameIgnoreCase(username.trim())
                .ifPresent(user -> {

                    int attempts =
                            user.getFailedLoginAttempts() == null
                                    ? 1
                                    : user.getFailedLoginAttempts() + 1;

                    user.setFailedLoginAttempts(attempts);

                    if (attempts >= maxFailedAttempts) {

                        UserStatusEntity lockedStatus =
                                userStatusRepository
                                        .findByCodeIgnoreCase("LOCKED")
                                        .orElseThrow();

                        user.setStatus(lockedStatus);
                    }

                    userRepository.save(user);
                });
    }

    @Transactional
    public void resetFailures(UserEntity user) {

        if (user.getFailedLoginAttempts() == null
                || user.getFailedLoginAttempts() != 0) {

            user.setFailedLoginAttempts(0);

            userRepository.save(user);
        }
    }
}