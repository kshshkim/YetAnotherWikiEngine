package dev.prvt.yawiki.core.wikititle.localcache.domain.initializer;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.localcache.domain.CacheStorage;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.WikiPageFixture.updateWikiPageRandomly;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CacheInitializerTest {
    private final WikiPageFactory wikiPageFactory = new WikiPageFactory();
    @Autowired
    EntityManager em;

    @Autowired
    CacheInitializer cacheInitializer;

    @Autowired
    CacheStorage cacheStorage;

    int TOTAL_TITLES = 30;

    List<WikiPageTitle> givenTitles = Stream.generate(WikiPageFixture::aWikiPageTitle)
            .limit(TOTAL_TITLES)
            .toList();

    @BeforeEach
    void init() {
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
    void test() {
        // when
        cacheInitializer.initialize();

        // then
        assertThat(givenTitles)
                .allMatch(cacheStorage::exists);
    }

}