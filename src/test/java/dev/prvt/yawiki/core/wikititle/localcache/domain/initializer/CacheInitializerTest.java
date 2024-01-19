package dev.prvt.yawiki.core.wikititle.localcache.domain.initializer;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.cache.domain.CacheStorage;
import dev.prvt.yawiki.core.wikititle.cache.domain.InitialCacheData;
import dev.prvt.yawiki.core.wikititle.cache.domain.initializer.CacheInitializer;
import dev.prvt.yawiki.core.wikititle.cache.domain.initializer.InitialCacheDataReader;
import dev.prvt.yawiki.core.wikititle.cache.infra.CacheStorageConcurrentHashMapImpl;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CacheInitializerTest {

    @Mock
    InitialCacheDataReader mockReader;

    CacheStorage cacheStorage = new CacheStorageConcurrentHashMapImpl();

    CacheInitializer cacheInitializer;
    int TOTAL_TITLES = 30;

    List<WikiPageTitle> givenTitles = Stream.generate(WikiPageFixture::aWikiPageTitle)
            .limit(TOTAL_TITLES)
            .toList();

    @BeforeEach
    void init() {
        cacheInitializer = new CacheInitializer(mockReader, cacheStorage);
    }

    @Test
    void test() {
        LocalDateTime requestedTime = LocalDateTime.now();
        InitialCacheData<WikiPageTitle> initialCacheData = new InitialCacheData<>(givenTitles.stream(),
            givenTitles.size(),
            requestedTime.minusSeconds(30));
        given(mockReader.getInitialDataStream(requestedTime))
            .willReturn(initialCacheData);

        // when
        cacheInitializer.initialize(requestedTime);

        // then
        assertThat(cacheStorage.filterExistentTitles(givenTitles))
            .describedAs("캐시가 정상적으로 초기화되어 모든 제목이 존재하는 상황. 고로 결과물은 비어있어야함.")
            .isEmpty();
    }

}