package dev.prvt.yawiki.titleexistence.cache.config;

import dev.prvt.yawiki.titleexistence.cache.domain.CacheStorage;
import dev.prvt.yawiki.titleexistence.cache.infra.CacheStorageConcurrentHashMapImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheStorageConfig {

    @Bean
    public CacheStorage cacheStorage() {
        return new CacheStorageConcurrentHashMapImpl();
    }
}
