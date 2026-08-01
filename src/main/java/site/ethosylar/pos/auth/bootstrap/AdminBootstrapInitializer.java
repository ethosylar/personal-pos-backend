package site.ethosylar.pos.auth.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.ethosylar.pos.role.entity.RoleEntity;
import site.ethosylar.pos.role.repository.RoleRepository;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.entity.UserRoleEntity;
import site.ethosylar.pos.user.entity.UserStatusEntity;
import site.ethosylar.pos.user.repository.UserRepository;
import site.ethosylar.pos.user.repository.UserRoleRepository;
import site.ethosylar.pos.user.repository.UserStatusRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "pos.bootstrap.admin.enabled",
    havingValue = "true"
)
public class AdminBootstrapInitializer
        implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserStatusRepository
            userStatusRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${pos.bootstrap.admin.username}")
    private String username;

    @Value("${pos.bootstrap.admin.password}")
    private String password;

    @Value("${pos.bootstrap.admin.full-name}")
    private String fullName;

    @Override
    @Transactional
    public void run(
            ApplicationArguments args) {

        if (userRepository.count() > 0) {

            log.info(
                "Administrator bootstrap skipped: "
                + "users already exist."
            );

            return;
        }

        if (username == null
                || username.isBlank()
                || password == null
                || password.isBlank()) {

            throw new IllegalStateException(
                "Bootstrap administrator "
                + "username/password are required."
            );
        }

        UserStatusEntity activeStatus =
                userStatusRepository
                    .findByCodeIgnoreCase("ACTIVE")
                    .orElseThrow();

        RoleEntity adminRole =
                roleRepository
                    .findByCodeIgnoreCase("ADMIN")
                    .orElseThrow();

        UserEntity admin =
                new UserEntity();

        admin.setUsername(username.trim());
        admin.setPasswordHash(
                passwordEncoder.encode(password)
        );
        admin.setFullName(fullName.trim());
        admin.setStatus(activeStatus);
        admin.setFailedLoginAttempts(0);

        admin = userRepository.save(admin);

        UserRoleEntity assignment =
                new UserRoleEntity();

        assignment.setUser(admin);
        assignment.setRole(adminRole);

        userRoleRepository.save(assignment);

        log.info(
            "Initial administrator account created."
        );
    }
}