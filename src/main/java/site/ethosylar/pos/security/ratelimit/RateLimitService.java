package site.ethosylar.pos.security.ratelimit;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

@Service
public class RateLimitService {

	private static final long LOGIN_CAPACITY = 5;

	private final Cache<String, Bucket> loginBuckets = Caffeine.newBuilder().maximumSize(10_000)
			.expireAfterAccess(30, TimeUnit.MINUTES).build();

	public ConsumptionProbe tryConsumeLogin(String clientKey) {

		Bucket bucket = loginBuckets.get(clientKey, key -> createLoginBucket());

		return bucket.tryConsumeAndReturnRemaining(1);
	}

	private Bucket createLoginBucket() {

		return Bucket.builder()
				.addLimit(
						limit -> limit.capacity(LOGIN_CAPACITY).refillIntervally(LOGIN_CAPACITY, Duration.ofMinutes(1)))
				.build();
	}
}