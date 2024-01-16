package dev.prvt.yawiki.core.wikititle.cache.infra.updater;


import dev.prvt.yawiki.core.wikititle.cache.exception.CacheNotInitializedException;
import dev.prvt.yawiki.core.wikititle.cache.domain.CacheStorage;
import dev.prvt.yawiki.core.wikititle.cache.domain.updater.RemoteReadCursorProvider;
import java.time.LocalDateTime;

/**
 * <p>트랜잭션 지연으로 인한 누락을 고려하여, 읽기 시작하는 지점에 마진값을 주는 구현체.</p>
 * <p>중복 삽입, 삭제는 고려되지 않았음. Cuckoo Filter, Counting Bloom Filter 를 사용하는 경우 새로운 구현체가 필요.</p>
 */
public class RemoteReadCursorProviderImpl implements RemoteReadCursorProvider {

    private final long readMarginInSeconds;
    private final CacheStorage cacheStorage;

    public RemoteReadCursorProviderImpl(long readMarginInSeconds, CacheStorage cacheStorage) {
        this.readMarginInSeconds = readMarginInSeconds;
        this.cacheStorage = cacheStorage;
    }

    @Override
    public ReadCursor getReadCursor() {
        if (!cacheStorage.isInitialized()) {
            throw new CacheNotInitializedException("cache not initialized");
        }
        return new ReadCursor(
            cacheStorage.getLastUpdatedAt().minusSeconds(readMarginInSeconds),
            LocalDateTime.now().plusDays(1)
        );
    }
}
