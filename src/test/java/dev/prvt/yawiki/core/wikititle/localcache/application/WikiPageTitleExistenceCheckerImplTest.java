package dev.prvt.yawiki.core.wikititle.localcache.application;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.cache.application.WikiPageTitleExistenceCheckerImpl;
import dev.prvt.yawiki.core.wikititle.cache.domain.InitialCacheData;
import dev.prvt.yawiki.core.wikititle.cache.domain.CacheStorage;
import dev.prvt.yawiki.core.wikititle.cache.infra.CacheStorageConcurrentHashMapImpl;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WikiPageTitleExistenceCheckerImplTest {

    CacheStorage cacheStorage;

    WikiPageTitleExistenceCheckerImpl titleExistenceChecker;

    List<WikiPageTitle> given = Stream.generate(WikiPageFixture::aWikiPageTitle).limit(20).toList();

    @BeforeEach
    void init() {
        cacheStorage = new CacheStorageConcurrentHashMapImpl();
        cacheStorage.init(new InitialCacheData<>(given.stream(), given.size(), LocalDateTime.now()));
        titleExistenceChecker = new WikiPageTitleExistenceCheckerImpl(cacheStorage);
    }

    @Test
    void filterExistingTitles_collection() {
        // when
        List<WikiPageTitle> nonExistTitles = Stream.generate(WikiPageFixture::aWikiPageTitle).limit(20).toList();
        List<WikiPageTitle> argument = new ArrayList<>();

        argument.addAll(nonExistTitles);
        argument.addAll(given);

        Collection<WikiPageTitle> result = titleExistenceChecker.filterExistentTitles(argument);

        // then
        assertThat(result)
                .describedAs("존재하지 않는 제목만 반환.")
                .containsExactlyInAnyOrderElementsOf(nonExistTitles);
    }

}