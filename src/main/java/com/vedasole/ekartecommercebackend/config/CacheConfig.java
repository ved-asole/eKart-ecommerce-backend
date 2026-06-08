package com.vedasole.ekartecommercebackend.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's annotation-driven cache management.
 * <p>
 * Kept separate from the main application class so that sliced tests
 * (e.g. {@code @DataJpaTest}) do not pull in caching infrastructure and
 * require a {@code CacheManager} bean.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
