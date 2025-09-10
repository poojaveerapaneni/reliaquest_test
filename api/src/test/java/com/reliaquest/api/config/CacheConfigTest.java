package com.reliaquest.api.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

class CacheConfigTest {

    @Test
    void testCacheManagerBeanCreation() {
        CacheConfig config = new CacheConfig();
        CacheManager cacheManager = config.cacheManager();

        assertNotNull(cacheManager);
        assertTrue(cacheManager instanceof ConcurrentMapCacheManager);

        // Verify that the cache "employees" exists
        assertNotNull(cacheManager.getCache("employees"));
    }
}
