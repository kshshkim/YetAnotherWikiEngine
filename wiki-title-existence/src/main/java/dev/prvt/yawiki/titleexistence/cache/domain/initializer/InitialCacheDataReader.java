package dev.prvt.yawiki.titleexistence.cache.domain.initializer;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.titleexistence.cache.domain.InitialCacheData;
import java.time.LocalDateTime;

/**
 * <p>캐시 초기화시 필요한 전체 위키 제목 목록 Reader</p>
 * <p>제목의 수가 많지 않은 경우 실시간 DB 쿼리로 처리할 수 있으나, 제목의 수가 많은 경우 트랜잭션이 장기화될 수 있기 때문에 파일로 따로 뽑는 것을 고려해야함.</p>
 */
public interface InitialCacheDataReader {

    /**
     * 실시간 DB 쿼리로 구현하는 경우, 트랜잭션 격리 수준과 커넥션 유지(Stream 활용)에 유의하여야함.
     * @param requestedTime 스냅샷 요청 시점. 스냅샷 이후 적절한 시점부터 변경 로그를 읽어오는데 사용됨.
     * @return {@link InitialCacheData}
     */
    InitialCacheData<WikiPageTitle> getInitialDataStream(LocalDateTime requestedTime);
}
