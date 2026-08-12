package site.ethosylar.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.security.handler.RestAccessDeniedHandler;
import site.ethosylar.pos.security.handler.RestAuthenticationEntryPoint;
import site.ethosylar.pos.security.ratelimit.LoginRateLimitFilter;
import site.ethosylar.pos.security.ratelimit.RateLimitService;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, RestAuthenticationEntryPoint authenticationEntryPoint,
			RestAccessDeniedHandler accessDeniedHandler, RateLimitService rateLimitService, ObjectMapper objectMapper)
			throws Exception {

		LoginRateLimitFilter rateLimitFilter = new LoginRateLimitFilter(rateLimitService, objectMapper);
		http.csrf(csrf -> csrf.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler))

				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(ApiPaths.V1 + "/auth/login", "/actuator/health").permitAll()

						.anyRequest().authenticated())
				.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)

				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

		return http.build();
	}

	@Bean
	AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

		provider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(provider);
	}
}