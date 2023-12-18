package dev.prvt.yawiki.core.wikititle.localcache.infra.updater;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.localcache.domain.LocalCacheStorage;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.LocalCacheWriter;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangeLog;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * Set 자료형 기반 storage 를 가정한 LocalCacheWriter 구현체.
 * Set 자료형의 특성상, 존재하는 원소를 반복해서 추가하거나, 존재하지 않는 원소를 제거하는 작업이 일어나더라도 문제가 없음.
 * 따라서 중복 작업을 고려하지 않아도 됨.
 */
@Component
@RequiredArgsConstructor
public class LocalCacheWriterImpl implements LocalCacheWriter {
    private final LocalCacheStorage localCacheStorage;

    public void write(List<RemoteChangeLog> logs) {
        if (logs.isEmpty()) {
            return;
        }
        writeTitles(logs);
        updateLastUpdatedAt(logs);
    }

    private void writeTitles(List<RemoteChangeLog> logs) {
        logs.forEach(
                writeToLocalStore()
        );
    }

    private void updateLastUpdatedAt(List<RemoteChangeLog> logs) {
        localCacheStorage.setLastUpdatedAt(
                getLastElement(logs).timestamp()
        );
    }

    private static int getLastIndex(List<?> list) {
        return list.size() - 1;
    }

    private static <T> T getLastElement(List<T> list) {
        return list.get(getLastIndex(list));
    }

    @NotNull
    private Consumer<RemoteChangeLog> writeToLocalStore() {
        return updateLog -> {
            WikiPageTitle wikiPageTitle = updateLog.title();
            switch (updateLog.changeType()) {
                case DELETED -> localCacheStorage.remove(wikiPageTitle);
                case CREATED -> localCacheStorage.add(wikiPageTitle);
            }
        };
    }

}
