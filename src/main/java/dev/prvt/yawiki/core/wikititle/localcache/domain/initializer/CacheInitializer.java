package dev.prvt.yawiki.core.wikititle.localcache.domain.initializer;

import dev.prvt.yawiki.core.wikititle.localcache.domain.CacheStorage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CacheInitializer {
    private final InitialCacheDataReader initialCacheDataReader;
    private final CacheStorage cacheStorage;

    public void initialize() {
        cacheStorage.init(initialCacheDataReader.getInitialDataStream());
    }
}
