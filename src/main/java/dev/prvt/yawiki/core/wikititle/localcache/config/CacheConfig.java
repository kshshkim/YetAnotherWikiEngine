package dev.prvt.yawiki.core.wikititle.localcache.config;

import dev.prvt.yawiki.core.wikititle.localcache.domain.*;
import dev.prvt.yawiki.core.wikititle.localcache.domain.initializer.CacheInitializer;
import dev.prvt.yawiki.core.wikititle.localcache.domain.initializer.InitialCacheDataReader;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.CacheUpdater;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.CacheWriter;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangesReader;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangesRepository;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteReadCursorProvider;
import dev.prvt.yawiki.core.wikititle.localcache.infra.initializer.InitialCacheDataReaderImpl;
import dev.prvt.yawiki.core.wikititle.localcache.infra.updater.RemoteReadCursorProviderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class CacheConfig {
    private final CacheStorage cacheStorage;
    private final CacheWriter cacheWriter;
    private final EntityManager entityManager;
    private final RemoteChangesRepository remoteChangesRepository;

    @Value("${yawiki.localCache.readMarginInSeconds:30}")
    private int readMarginInSeconds;

    @Bean
    public InitialCacheDataReader initialCacheDataReader() {
        return new InitialCacheDataReaderImpl(entityManager, readMarginInSeconds);
    }

    @Bean
    public CacheInitializer cacheInitializer() {
        return new CacheInitializer(initialCacheDataReader(), cacheStorage);
    }

    @Bean
    public RemoteChangesReader remoteChangesReader() {
        return new RemoteChangesReader(remoteReadCursorProvider(), remoteChangesRepository);
    }

    @Bean
    public CacheUpdater cacheUpdater() {
        return new CacheUpdater(remoteChangesReader(), cacheWriter);
    }

    @Bean
    public RemoteReadCursorProvider remoteReadCursorProvider() {
        return new RemoteReadCursorProviderImpl(readMarginInSeconds, cacheStorage);
    }
}
