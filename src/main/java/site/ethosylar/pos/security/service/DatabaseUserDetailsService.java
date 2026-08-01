package site.ethosylar.pos.security.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;
import site.ethosylar.pos.user.repository.UserRoleRepository;

@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserEntity user = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found."));

		String statusCode = user.getStatus().getCode();

		boolean active = "ACTIVE".equalsIgnoreCase(statusCode);

		boolean locked = "LOCKED".equalsIgnoreCase(statusCode);

		List<GrantedAuthority> authorities = userRoleRepository.findByUser_Id(user.getId()).stream()
				.filter(userRole -> Boolean.TRUE.equals(userRole.getRole().getActive()))
				.map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getCode()))
				.map(GrantedAuthority.class::cast).toList();

		return User.withUsername(user.getUsername()).password(user.getPasswordHash()).authorities(authorities)
				.disabled(!active).accountLocked(locked).build();
	}
}