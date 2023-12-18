package dev.prvt.yawiki.core.wikititle.localcache.domain.initializer;

import dev.prvt.yawiki.core.wikititle.localcache.domain.LocalCacheStorage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocalCacheInitializer {
    private final InitialCacheDataReader initialCacheDataReader;
    private final LocalCacheStorage localCacheStorage;

    public void initialize() {
        localCacheStorage.init(initialCacheDataReader.getInitialDataStream());
    }
}
