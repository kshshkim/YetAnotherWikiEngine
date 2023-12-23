package dev.prvt.yawiki.core.wikititle.localcache.infra.initializer;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.localcache.domain.InitialCacheData;
import dev.prvt.yawiki.core.wikititle.localcache.domain.initializer.InitialCacheDataReader;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.WikiPageFixture.updateWikiPageRandomly;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class InitialCacheDataReaderImplTest {
    private final WikiPageFactory wikiPageFactory = new WikiPageFactory();

    @Autowired
    EntityManager em;

    int TITLE_COUNT = 20;
    int READ_MARGIN = 60;
    List<WikiPageTitle> givenTitles = Stream.generate(WikiPageFixture::aWikiPageTitle).limit(TITLE_COUNT).toList();

    InitialCacheDataReader initialCacheDataReader;

    @BeforeEach
    void init() {
        initialCacheDataReader = new InitialCacheDataReaderImpl(em, READ_MARGIN);
        List<WikiPage> wikiPages = givenTitles.stream()
                .map(
                        wpt -> {
                            WikiPage wikiPage = wikiPageFactory.create(wpt.title(), wpt.namespace());
                            updateWikiPageRandomly(wikiPage);
                            return wikiPage;
                        }
                )
                .toList();
        wikiPages.forEach(em::persist);
        em.flush();
        em.clear();
    }


    @Test
    void getInitialDataStream() {
        LocalDateTime now = LocalDateTime.now();

        // when
        InitialCacheData<WikiPageTitle> initialDataStream = initialCacheDataReader.getInitialDataStream();

        // then
        List<WikiPageTitle> titles = initialDataStream.stream().toList();
        assertThat(titles)
                .describedAs("모든 타이틀을 가져옴")
                .containsExactlyInAnyOrderElementsOf(givenTitles);

        assertThat(initialDataStream.totalElements())
                .describedAs("총 원소 숫자가 적절히 입력됨")
                .isEqualTo(TITLE_COUNT);

        assertThat(initialDataStream.lastUpdatedAt())
                .describedAs("마진값을 감안하여 업데이트 수행 시점이 기록됨.")
                .isAfter(now.minusSeconds(READ_MARGIN))
                .isBefore(now.minusSeconds(READ_MARGIN).plusSeconds(1));
    }
}