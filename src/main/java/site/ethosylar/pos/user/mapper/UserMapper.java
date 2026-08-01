package site.ethosylar.pos.user.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.entity.UserRoleEntity;
import site.ethosylar.pos.user.response.UserDetailResponse;
import site.ethosylar.pos.user.response.UserResponse;
import site.ethosylar.pos.user.response.UserRoleResponse;
import site.ethosylar.pos.user.response.UserStatusResponse;

@Component
public class UserMapper {

	public UserResponse toResponse(UserEntity user, List<UserRoleEntity> userRoles) {

		return UserResponse.builder().id(user.getId()).username(user.getUsername()).fullName(user.getFullName())
				.email(user.getEmail()).status(toStatusResponse(user)).roles(toRoleResponses(userRoles))
				.lastLoginAt(user.getLastLoginAt()).createdAt(user.getCreatedAt()).updatedAt(user.getUpdatedAt())
				.build();
	}

	public UserDetailResponse toDetailResponse(UserEntity user, List<UserRoleEntity> userRoles) {

		return UserDetailResponse.builder().id(user.getId()).username(user.getUsername()).fullName(user.getFullName())
				.email(user.getEmail()).status(toStatusResponse(user)).roles(toRoleResponses(userRoles))
				.failedLoginAttempts(user.getFailedLoginAttempts()).lastLoginAt(user.getLastLoginAt())
				.passwordChangedAt(user.getPasswordChangedAt()).createdAt(user.getCreatedAt())
				.updatedAt(user.getUpdatedAt()).build();
	}

	private UserStatusResponse toStatusResponse(UserEntity user) {

		if (user.getStatus() == null) {
			return null;
		}

		return UserStatusResponse.builder().id(user.getStatus().getId()).code(user.getStatus().getCode())
				.name(user.getStatus().getName()).build();
	}

	private List<UserRoleResponse> toRoleResponses(List<UserRoleEntity> userRoles) {

		if (userRoles == null) {
			return Collections.emptyList();
		}

		return userRoles.stream().map(userRole -> UserRoleResponse.builder().id(userRole.getRole().getId())
				.code(userRole.getRole().getCode()).name(userRole.getRole().getName()).build()).toList();
	}
}