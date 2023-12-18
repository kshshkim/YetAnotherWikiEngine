package dev.prvt.yawiki.core.wikititle.localcache.domain.initializer;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.localcache.domain.InitialCacheData;

/**
 * <p>캐시 초기화시 필요한 전체 위키 제목 목록 Reader</p>
 * <p>제목의 수가 많지 않은 경우 실시간 DB 쿼리로 처리할 수 있으나, 제목의 수가 많은 경우 트랜잭션이 장기화될 수 있기 때문에 파일로 따로 뽑는 것을 고려해야함.</p>
 */
public interface InitialCacheDataReader {
    InitialCacheData<WikiPageTitle> getInitialDataStream();
}
