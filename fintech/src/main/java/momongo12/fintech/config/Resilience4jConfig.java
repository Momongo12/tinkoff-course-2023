package momongo12.fintech.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author Momongo12
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class Resilience4jConfig {

    private final RateLimiterRegistry rateLimiterRegistry;

    @Bean
    public RateLimiter rateLimitWithCustomConfig() {
        RateLimiterConfig customConfig = RateLimiterConfig.custom()
                .limitForPeriod(1000000)
                .limitRefreshPeriod(Duration.ofDays(31))
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        return rateLimiterRegistry.rateLimiter("weatherApiRateLimiter", customConfig);
    }
}
