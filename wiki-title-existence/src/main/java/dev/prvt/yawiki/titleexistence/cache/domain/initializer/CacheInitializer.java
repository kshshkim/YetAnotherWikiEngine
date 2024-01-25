package dev.prvt.yawiki.titleexistence.cache.domain.initializer;

import dev.prvt.yawiki.titleexistence.cache.domain.CacheStorage;
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
