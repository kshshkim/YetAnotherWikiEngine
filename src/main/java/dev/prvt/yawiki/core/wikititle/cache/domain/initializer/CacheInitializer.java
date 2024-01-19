package dev.prvt.yawiki.core.wikititle.cache.domain.initializer;

import dev.prvt.yawiki.core.wikititle.cache.domain.CacheStorage;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CacheInitializer {
    private final InitialCacheDataReader initialCacheDataReader;
    private final CacheStorage cacheStorage;

    public void initialize(LocalDateTime requestedTime) {
        cacheStorage.init(initialCacheDataReader.getInitialDataStream(requestedTime));
    }
}
