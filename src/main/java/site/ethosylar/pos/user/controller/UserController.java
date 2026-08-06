package site.ethosylar.pos.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.user.request.AssignUserRolesRequest;
import site.ethosylar.pos.user.request.ChangeUserStatusRequest;
import site.ethosylar.pos.user.request.CreateUserRequest;
import site.ethosylar.pos.user.request.ResetUserPasswordRequest;
import site.ethosylar.pos.user.request.UpdateUserRequest;
import site.ethosylar.pos.user.response.UserDetailResponse;
import site.ethosylar.pos.user.response.UserResponse;
import site.ethosylar.pos.user.service.UserService;

@RestController
@RequestMapping(ApiPaths.V1 + "/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'USER_MANAGEMENT', " + "'VIEW')")
	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers() {

		return ResponseEntity.ok(userService.getAllUsers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDetailResponse> getUserById(@PathVariable Long id) {

		return ResponseEntity.ok(userService.getUserById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'USER_MANAGEMENT', " + "'CREATE')")
	@PostMapping
	public ResponseEntity<UserDetailResponse> createUser(@Valid @RequestBody CreateUserRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserDetailResponse> updateUser(@PathVariable Long id,
			@Valid @RequestBody UpdateUserRequest request) {

		return ResponseEntity.ok(userService.updateUser(id, request));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<UserDetailResponse> changeUserStatus(@PathVariable Long id,
			@Valid @RequestBody ChangeUserStatusRequest request) {

		return ResponseEntity.ok(userService.changeUserStatus(id, request));
	}

	@PutMapping("/{id}/roles")
	public ResponseEntity<UserDetailResponse> assignRoles(@PathVariable Long id,
			@Valid @RequestBody AssignUserRolesRequest request) {

		return ResponseEntity.ok(userService.assignRoles(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'USER_MANAGEMENT', "
			+ "'RESET_PASSWORD')")
	@PatchMapping("/{id}/password/reset")
	public ResponseEntity<Void> resetPassword(@PathVariable Long id,
			@Valid @RequestBody ResetUserPasswordRequest request) {

		userService.resetPassword(id, request);

		return ResponseEntity.noContent().build();
	}
}