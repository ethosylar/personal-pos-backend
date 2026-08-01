package site.ethosylar.pos.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.auth.request.ChangePasswordRequest;
import site.ethosylar.pos.auth.request.LoginRequest;
import site.ethosylar.pos.auth.response.AuthUserResponse;
import site.ethosylar.pos.auth.response.LoginResponse;
import site.ethosylar.pos.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

		return ResponseEntity.ok(authService.login(request));
	}

	@GetMapping("/me")
	public ResponseEntity<AuthUserResponse> me(Authentication authentication) {

		return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
	}

	@PostMapping("/change-password")
	public ResponseEntity<Void> changePassword(Authentication authentication,
			@Valid @RequestBody ChangePasswordRequest request) {

		authService.changePassword(authentication.getName(), request);

		return ResponseEntity.noContent().build();
	}
}