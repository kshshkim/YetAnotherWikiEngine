package dev.prvt.yawiki.core.wikititle.localcache.domain.updater;

import dev.prvt.yawiki.core.wikititle.localcache.domain.LocalCacheStorage;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class LocalCacheUpdater {
    private final LocalCacheStorage localStore;
    private final RemoteChangesReader remoteChangesReader;
    private final LocalCacheWriter localCacheWriter;
    private final long readMarginInSeconds;

    /**
     * @param localStore          로컬 캐시 객체
     * @param remoteChangesReader 원격 변동 내역 Reader
     * @param localCacheWriter    로컬 캐시 Writer
     * @param readMarginInSeconds 트랜잭션이 늦게 완료되어 조회되지 않는 레코드가 존재할 수 있음. 이를 방지하기 위해서 조회시 마진 값을 줌. 트랜잭션 time out 을 고려하여 설정할 것
     */
    public LocalCacheUpdater(
            LocalCacheStorage localStore,
            RemoteChangesReader remoteChangesReader,
            LocalCacheWriter localCacheWriter,
            long readMarginInSeconds  // 트랜잭션 time out 을 고려하여 설정할 것
    ) {
        this.localStore = localStore;
        this.remoteChangesReader = remoteChangesReader;
        this.localCacheWriter = localCacheWriter;
        this.readMarginInSeconds = readMarginInSeconds;
    }

    public void update() {
        checkInitialized();
        write(readChanges());
    }

    private void checkInitialized() {
        if (this.localStore.getLastUpdatedAt() == null) {
            throw new IllegalStateException("local cache data has not been initialized");
        }
    }

    private void write(List<RemoteChangeLog> readerResult) {
        log.debug("readerResultSize={}, readerResultLastElement={}", readerResult.size(), readerResult.isEmpty() ? null : readerResult.get(readerResult.size() - 1));
        localCacheWriter.write(readerResult);
    }

    /**
     * <p>서비스 특성상 짧은 기간에 큰 부하를 줄 만큼의 이벤트 로그가 쌓일 일은 없을 것으로 생각되지만, 혹여 그런 일이 일어난다면 잘라서 조회해야할 필요가 있음.</p>
     * <p>
     * 트랜잭션 지연 등으로 인해, 읽는 시점에 누락되는 내역이 존재할 수 있음. 이런 문제를 해결하기 위해 변경 로그를 읽어오는 커서에 대해 마진 값을 줌.
     * 존재-비존재 두 값만 존재하기 때문에, 작업이 중복 실행되더라도 순서만 맞게 실행된다면 결과적으로 데이터 정합성에 문제가 생기지 않음.
     * 그러나, Counting Bloom Filter 기반으로 구현된 경우에는 작업이 중복 실행되어선 안 되며, 중복 실행을 막을 추가적인 로직을 구현해야함.
     * </p>
     */
    private List<RemoteChangeLog> readChanges() {
        return remoteChangesReader.readUpdated(
                localStore.getLastUpdatedAt().minusSeconds(readMarginInSeconds),
                LocalDateTime.now()
        );
    }
}
