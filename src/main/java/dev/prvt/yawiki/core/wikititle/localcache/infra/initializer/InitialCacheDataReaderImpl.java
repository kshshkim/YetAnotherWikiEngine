package dev.prvt.yawiki.core.wikititle.localcache.infra.initializer;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.QWikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.localcache.domain.InitialCacheData;
import dev.prvt.yawiki.core.wikititle.localcache.domain.initializer.InitialCacheDataReader;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static dev.prvt.yawiki.core.wikipage.domain.model.QWikiPage.wikiPage;


/**
 * 실시간 DB 쿼리를 통해 초기 캐시 데이터를 읽어오는 구현체.
 * 만약 문서의 숫자가 매우 많아질 경우, 실시간 DB 쿼리를 피하는 방법으로 재구현해야함.
 */
public class InitialCacheDataReaderImpl implements InitialCacheDataReader {

    private final JPAQueryFactory queryFactory;
    /**
     * 트랜잭션 지연 등의 사유로 읽기에 누락이 발생하는 상황을 대비하기 위한 마진 값.
     * hashset 구현의 경우, 상태가 두 가지 뿐이기 때문에 중복 실행되어도 무방함.
     */
    private final int readMargin;

    public InitialCacheDataReaderImpl(EntityManager em, int readMarginInSeconds) {
        this.queryFactory = new JPAQueryFactory(em);
        this.readMargin = readMarginInSeconds;
    }

    /**
     * 작업 시작 시점 기준으로, 트랜잭션 타임아웃을 고려하여 마진을 설정함.
     * @return
     */
    private LocalDateTime getCacheUpdatedTime() {
        return LocalDateTime.now().minusSeconds(readMargin);
    }

    @Override
    public InitialCacheData<WikiPageTitle> getInitialDataStream() {
        LocalDateTime cacheUpdatedTime = getCacheUpdatedTime();

        int count = queryFactory
                .select(wikiPage.count())
                .from(wikiPage)
                .where(wikiPage.active.isTrue())
                .fetchOne()
                .intValue();

        Stream<WikiPageTitle> stream = queryFactory
                .select(new QWikiPageTitle(wikiPage.title, wikiPage.namespace))
                .from(wikiPage)
                .stream();

        return new InitialCacheData<>(stream, count, cacheUpdatedTime);
    }
}
