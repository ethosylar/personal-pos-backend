package site.ethosylar.pos.security.ratelimit;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import site.ethosylar.pos.common.error.ApiErrorCode;
import site.ethosylar.pos.common.error.ApiErrorResponse;
import tools.jackson.databind.ObjectMapper;

public class LoginRateLimitFilter extends OncePerRequestFilter {

	private final RateLimitService rateLimitService;

	private final ObjectMapper objectMapper;

	public LoginRateLimitFilter(RateLimitService rateLimitService, ObjectMapper objectMapper) {

		this.rateLimitService = rateLimitService;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (!isLoginRequest(request)) {

			filterChain.doFilter(request, response);

			return;
		}

		String clientKey = resolveClientKey(request);

		ConsumptionProbe probe = rateLimitService.tryConsumeLogin(clientKey);

		if (probe.isConsumed()) {

			response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));

			filterChain.doFilter(request, response);

			return;
		}

		long retryAfterSeconds = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));

		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		response.setCharacterEncoding("UTF-8");

		response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

		writeRateLimitError(request, response);
	}

	private boolean isLoginRequest(HttpServletRequest request) {

		return "POST".equalsIgnoreCase(request.getMethod()) && "/api/v1/auth/login".equals(request.getRequestURI());
	}

	private String resolveClientKey(HttpServletRequest request) {

		return request.getRemoteAddr();
	}

	private void writeRateLimitError(HttpServletRequest request, HttpServletResponse response) throws IOException {

		/*
		 * Adapt this constructor/builder to your actual ApiErrorResponse
		 * implementation.
		 */
		String message = "Too many login attempts. " + "Please try again later.";
		ApiErrorResponse error = ApiErrorResponse.builder()

				.timestamp(OffsetDateTime.now())

				.status(HttpStatus.TOO_MANY_REQUESTS.value())

				.error(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())

				.code(ApiErrorCode.RATE_LIMIT_EXCEEDED.name())

				.message(message)

				.path(request.getRequestURI())

				.fieldErrors(List.of())

				.traceId(null)

				.build();

		objectMapper.writeValue(response.getWriter(), error);
	}
}