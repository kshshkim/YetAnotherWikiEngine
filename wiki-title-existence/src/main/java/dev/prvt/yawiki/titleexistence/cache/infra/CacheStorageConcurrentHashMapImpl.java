package dev.prvt.yawiki.titleexistence.cache.infra;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.titleexistence.cache.domain.CacheStorage;
import dev.prvt.yawiki.titleexistence.cache.domain.InitialCacheData;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * <p>
 * 복수의 스레드가 한 번에 접근하는 상황은 가정하지 않았으나, ConcurrentHashMap 기반으로 구현되어 동시성은 보장됨.
 * </p>
 * <p>
 * Set 자료형을 이용하지만, 드물게 발생하는 거짓 긍정 응답은 어느정도 허용되는 점,
 * 존재하는지 여부만 알면 되고, 내부의 내용물을 조회할 필요는 없다는 점을 고려한다면 counting bloom filter 활용에 유리한 조건이라고 생각됨.
 * </p>
 */
public class CacheStorageConcurrentHashMapImpl implements CacheStorage {
    /**
     * 마지막으로 캐시가 갱신된 시점.
     */
    private volatile LocalDateTime lastUpdatedAt;

    /**
     * 내부 해시셋. 쓰기 작업에 있어서 동시 접근이 일어나지 않는다는 가정으로 구현한 것이지만, ConcurrentHashMap 기반으로 구현되어 동시성은 보장됨.
     */
    private Set<WikiPageTitle> titleSet;

    @Override
    public void add(WikiPageTitle title) {
        titleSet.add(title);
    }

    @Override
    public void addAll(Collection<WikiPageTitle> titles) {
        titleSet.addAll(titles);
    }

    @Override
    public void remove(WikiPageTitle title) {
        titleSet.remove(title);
    }

    @Override
    public void removeAll(Collection<WikiPageTitle> titles) {
        titleSet.removeAll(titles);
    }

    @Override
    public boolean exists(WikiPageTitle title) {
        return titleSet.contains(title);
    }

    @Override
    public boolean isInitialized() {
        return getLastUpdatedAt() != null;
    }

    @Override
    public Collection<WikiPageTitle> filterExistentTitles(Collection<WikiPageTitle> titles) {
        return titles.stream()
                   .filter(title -> !this.exists(title))
                   .toList();
    }

    @Override
    public Collection<WikiPageTitle> filterExistentTitles(Stream<WikiPageTitle> titles) {
        return titles.filter(title -> !this.exists(title)).toList();
    }

    @Override
    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    @Override
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Override
    public void init(InitialCacheData<WikiPageTitle> initialStreamData) {
        this.titleSet = ConcurrentHashMap.newKeySet(initialStreamData.totalElements());
        initialStreamData.stream().forEach(this::add);
        this.setLastUpdatedAt(initialStreamData.lastUpdatedAt());
    }
}
