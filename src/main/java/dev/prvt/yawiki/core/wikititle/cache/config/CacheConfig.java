package dev.prvt.yawiki.core.wikititle.cache.config;

import dev.prvt.yawiki.core.wikititle.cache.domain.CacheStorage;
import dev.prvt.yawiki.core.wikititle.cache.domain.initializer.CacheInitializer;
import dev.prvt.yawiki.core.wikititle.cache.domain.initializer.InitialCacheDataReader;
import dev.prvt.yawiki.core.wikititle.cache.domain.updater.CacheUpdater;
import dev.prvt.yawiki.core.wikititle.cache.domain.updater.CacheWriter;
import dev.prvt.yawiki.core.wikititle.cache.domain.updater.RemoteChangesReader;
import dev.prvt.yawiki.core.wikititle.cache.domain.updater.RemoteChangesRepository;
import dev.prvt.yawiki.core.wikititle.cache.domain.updater.RemoteReadCursorProvider;
import dev.prvt.yawiki.core.wikititle.cache.infra.initializer.InitialCacheDataReaderJdbcImpl;
import dev.prvt.yawiki.core.wikititle.cache.infra.updater.RemoteReadCursorProviderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class CacheConfig {
    private final CacheStorage cacheStorage;
    private final CacheWriter cacheWriter;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RemoteChangesRepository remoteChangesRepository;

    @Value("${yawiki.localCache.readMarginInSeconds:30}")
    private int readMarginInSeconds;

    @Bean
    public InitialCacheDataReader initialCacheDataReader() {
        return new InitialCacheDataReaderJdbcImpl(namedParameterJdbcTemplate, readMarginInSeconds);
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
