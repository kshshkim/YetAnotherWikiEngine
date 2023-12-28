package dev.prvt.yawiki.core.wikititle.localcache.config;

import dev.prvt.yawiki.core.wikititle.localcache.domain.*;
import dev.prvt.yawiki.core.wikititle.localcache.domain.initializer.LocalCacheInitializer;
import dev.prvt.yawiki.core.wikititle.localcache.domain.initializer.InitialCacheDataReader;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.LocalCacheUpdater;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.LocalCacheWriter;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangesReader;
import dev.prvt.yawiki.core.wikititle.localcache.infra.initializer.InitialCacheDataReaderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class LocalCacheConfig {
    private final LocalCacheStorage localCacheStorage;
    private final LocalCacheWriter localCacheWriter;
    private final RemoteChangesReader remoteChangesReader;
    private final EntityManager entityManager;
    @Value("${yawiki.localCache.readMarginInSeconds:30}")
    private int readMarginInSeconds;

    @Bean
    public InitialCacheDataReader initialCacheDataReader() {
        return new InitialCacheDataReaderImpl(entityManager, readMarginInSeconds);
    }

    @Bean
    public LocalCacheInitializer cacheInitializer() {
        return new LocalCacheInitializer(initialCacheDataReader(), localCacheStorage);
    }

    @Bean
    public LocalCacheUpdater cacheUpdater() {
        return new LocalCacheUpdater(localCacheStorage, remoteChangesReader, localCacheWriter, readMarginInSeconds);
    }
}
