package site.ethosylar.pos.security.jwt;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.user.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

	private final JwtEncoder jwtEncoder;

	@Value("${pos.security.jwt.issuer}")
	private String issuer;

	@Value("${pos.security.jwt.access-token-ttl}")
	private Duration accessTokenTtl;

	public String generateAccessToken(UserEntity user) {

		Instant now = Instant.now();

		JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).issuedAt(now).expiresAt(now.plus(accessTokenTtl))
				.subject(user.getUsername()).claim("uid", user.getId()).build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public long getExpiresInSeconds() {
		return accessTokenTtl.toSeconds();
	}
}