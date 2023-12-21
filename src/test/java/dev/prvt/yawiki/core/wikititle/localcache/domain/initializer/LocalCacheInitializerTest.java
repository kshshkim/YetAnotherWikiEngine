package dev.prvt.yawiki.core.wikititle.localcache.domain.initializer;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.localcache.domain.LocalCacheStorage;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LocalCacheInitializerTest {
    private final WikiPageFactory wikiPageFactory = new WikiPageFactory();
    @Autowired
    EntityManager em;

    @Autowired
    LocalCacheInitializer localCacheInitializer;

    @Autowired
    LocalCacheStorage localCacheStorage;

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
                            wikiPage.update(UUID.randomUUID(), randString(), randString());
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
        localCacheInitializer.initialize();

        // then
        assertThat(givenTitles)
                .allMatch(localCacheStorage::exists);
    }

}