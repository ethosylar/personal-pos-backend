package site.ethosylar.pos.user.service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.role.entity.RoleEntity;
import site.ethosylar.pos.role.repository.RoleRepository;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.entity.UserRoleEntity;
import site.ethosylar.pos.user.entity.UserStatusEntity;
import site.ethosylar.pos.user.mapper.UserMapper;
import site.ethosylar.pos.user.repository.UserRepository;
import site.ethosylar.pos.user.repository.UserRoleRepository;
import site.ethosylar.pos.user.repository.UserStatusRepository;
import site.ethosylar.pos.user.request.AssignUserRolesRequest;
import site.ethosylar.pos.user.request.ChangeUserStatusRequest;
import site.ethosylar.pos.user.request.CreateUserRequest;
import site.ethosylar.pos.user.request.ResetUserPasswordRequest;
import site.ethosylar.pos.user.request.UpdateUserRequest;
import site.ethosylar.pos.user.response.UserDetailResponse;
import site.ethosylar.pos.user.response.UserResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> userMapper.toResponse(
                        user,
                        userRoleRepository.findByUser_Id(user.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(Long id) {

        UserEntity user = getUserEntity(id);

        return userMapper.toDetailResponse(
                user,
                userRoleRepository.findByUser_Id(id)
        );
    }

    public UserDetailResponse createUser(
            CreateUserRequest request) {

        validateUniqueUsername(request.getUsername());

        if (request.getEmail() != null
                && !request.getEmail().isBlank()) {

            validateUniqueEmail(request.getEmail());
        }

        UserStatusEntity status =
                userStatusRepository
                        .findById(request.getUserStatusId())
                        .orElseThrow(() ->
                            new ResourceNotFoundException(
                                "User status not found."
                            )
                        );

        UserEntity user = new UserEntity();

        user.setUsername(request.getUsername().trim());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizeEmail(request.getEmail()));
        user.setStatus(status);
        user.setFailedLoginAttempts(0);
        user.setPasswordChangedAt(OffsetDateTime.now());

        user = userRepository.save(user);

        if (request.getRoleIds() != null
                && !request.getRoleIds().isEmpty()) {

            synchronizeRoles(
                    user,
                    request.getRoleIds()
            );
        }

        return userMapper.toDetailResponse(
                user,
                userRoleRepository.findByUser_Id(user.getId())
        );
    }

    public UserDetailResponse updateUser(
            Long id,
            UpdateUserRequest request) {

        UserEntity user = getUserEntity(id);

        String newEmail = normalizeEmail(request.getEmail());

        if (newEmail != null
                && !newEmail.equalsIgnoreCase(
                    user.getEmail() == null
                        ? ""
                        : user.getEmail()
                )
                && userRepository
                    .existsByEmailIgnoreCase(newEmail)) {

            throw new DuplicateResourceException(
                    "Email address already exists."
            );
        }

        user.setFullName(request.getFullName().trim());
        user.setEmail(newEmail);

        userRepository.save(user);

        return userMapper.toDetailResponse(
                user,
                userRoleRepository.findByUser_Id(id)
        );
    }

    public UserDetailResponse changeUserStatus(
            Long id,
            ChangeUserStatusRequest request) {

        UserEntity user = getUserEntity(id);

        UserStatusEntity status =
                userStatusRepository
                        .findById(request.getUserStatusId())
                        .orElseThrow(() ->
                            new ResourceNotFoundException(
                                "User status not found."
                            )
                        );

        user.setStatus(status);
        
        if ("ACTIVE".equalsIgnoreCase(status.getCode())) {
            user.setFailedLoginAttempts(0);
        }

        userRepository.save(user);

        return userMapper.toDetailResponse(
                user,
                userRoleRepository.findByUser_Id(id)
        );
    }

    public UserDetailResponse assignRoles(
            Long id,
            AssignUserRolesRequest request) {

        UserEntity user = getUserEntity(id);

        synchronizeRoles(
                user,
                request.getRoleIds()
        );

        return userMapper.toDetailResponse(
                user,
                userRoleRepository.findByUser_Id(id)
        );
    }

    private void synchronizeRoles(
            UserEntity user,
            List<Long> roleIds) {

        List<Long> requestedIds =
                roleIds == null
                        ? Collections.emptyList()
                        : roleIds.stream()
                            .distinct()
                            .toList();

        List<RoleEntity> roles =
                roleRepository.findAllById(requestedIds);

        if (roles.size() != requestedIds.size()) {

            throw new ResourceNotFoundException(
                    "One or more selected roles do not exist."
            );
        }

        List<UserRoleEntity> existing =
                userRoleRepository
                        .findByUser_Id(user.getId());

        List<UserRoleEntity> toDelete =
                existing.stream()
                        .filter(existingRole ->
                            !requestedIds.contains(
                                existingRole
                                    .getRole()
                                    .getId()
                            )
                        )
                        .toList();

        userRoleRepository.deleteAll(toDelete);

        for (RoleEntity role : roles) {

            boolean alreadyAssigned =
                    existing.stream()
                            .anyMatch(existingRole ->
                                existingRole
                                    .getRole()
                                    .getId()
                                    .equals(role.getId())
                            );

            if (!alreadyAssigned) {

                UserRoleEntity userRole =
                        new UserRoleEntity();

                userRole.setUser(user);
                userRole.setRole(role);

                /*
                 * assignedBy will be populated from
                 * the authenticated user later.
                 */
                userRole.setAssignedBy(null);

                userRoleRepository.save(userRole);
            }
        }
    }

    private UserEntity getUserEntity(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found."
                    )
                );
    }

    private void validateUniqueUsername(
            String username) {

        if (userRepository
                .existsByUsernameIgnoreCase(
                    username.trim()
                )) {

            throw new DuplicateResourceException(
                    "Username already exists."
            );
        }
    }

    private void validateUniqueEmail(
            String email) {

        if (userRepository
                .existsByEmailIgnoreCase(
                    email.trim()
                )) {

            throw new DuplicateResourceException(
                    "Email address already exists."
            );
        }
    }

    private String normalizeEmail(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }
    
    public void resetPassword(
            Long id,
            ResetUserPasswordRequest request) {

        UserEntity user = getUserEntity(id);

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new BusinessRuleException(
                    "Password and confirmation do not match."
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(
                    request.getNewPassword()
                )
        );

        user.setPasswordChangedAt(
                OffsetDateTime.now()
        );

        user.setFailedLoginAttempts(0);

        userRepository.save(user);
    }
}