package dev.prvt.yawiki.core.wikititle.localcache.domain;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * <p>존재하는 제목의 정보를 가지고 있는 스토리지 인터페이스.</p>
 * <p>값을 가지고 있는지 아닌지만 판단하면 됨. 제목을 다시 빼낼 필요는 없음.</p>
 * <p>서비스 특성상 거짓 긍정(false positive) 응답이 일부 발생하더라도 큰 문제는 아님. Bloom Filter 등을 활용한 구현도 고려할것.</p>
 */
public interface LocalCacheStorage {
    void add(WikiPageTitle title);

    void addAll(Collection<WikiPageTitle> titles);

    void remove(WikiPageTitle title);

    void removeAll(Collection<WikiPageTitle> titles);

    boolean exists(WikiPageTitle title);

    LocalDateTime getLastUpdatedAt();

    void setLastUpdatedAt(LocalDateTime lastUpdatedAt);

    void init(InitialCacheData<WikiPageTitle> initialCacheData);

}
