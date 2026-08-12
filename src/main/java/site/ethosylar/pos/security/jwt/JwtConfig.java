package site.ethosylar.pos.security.jwt;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtConfig {

	@Value("${pos.security.jwt.secret}")
	private String encodedSecret;

	@Value("${pos.security.jwt.issuer}")
	private String issuer;

	@Bean
	SecretKey jwtSecretKey() {

		byte[] keyBytes = Base64.getDecoder().decode(encodedSecret);

		if (keyBytes.length < 32) {
			throw new IllegalStateException("JWT secret must contain at least 256 bits.");
		}

		return new SecretKeySpec(keyBytes, "HmacSHA256");
	}

	@Bean
	JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {

		return NimbusJwtEncoder.withSecretKey(jwtSecretKey).algorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {

		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256)
				.build();

		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));

		return decoder;
	}
}