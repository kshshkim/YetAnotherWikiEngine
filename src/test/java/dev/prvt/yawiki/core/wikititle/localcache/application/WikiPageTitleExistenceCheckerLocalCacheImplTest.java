package dev.prvt.yawiki.core.wikititle.localcache.application;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.localcache.domain.InitialCacheData;
import dev.prvt.yawiki.core.wikititle.localcache.domain.LocalCacheStorage;
import dev.prvt.yawiki.core.wikititle.localcache.infra.LocalCacheStorageConcurrentHashMapImpl;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WikiPageTitleExistenceCheckerLocalCacheImplTest {

    LocalCacheStorage localCacheStorage;

    WikiPageTitleExistenceCheckerLocalCacheImpl titleExistenceChecker;

    List<WikiPageTitle> given = Stream.generate(WikiPageFixture::aWikiPageTitle).limit(20).toList();

    @BeforeEach
    void init() {
        localCacheStorage = new LocalCacheStorageConcurrentHashMapImpl();
        localCacheStorage.init(new InitialCacheData<>(given.stream(), given.size(), LocalDateTime.now()));
        titleExistenceChecker = new WikiPageTitleExistenceCheckerLocalCacheImpl(localCacheStorage);
    }

    @Test
    void filterExistingTitles_collection() {
        // when
        List<WikiPageTitle> nonExistTitles = Stream.generate(WikiPageFixture::aWikiPageTitle).limit(20).toList();
        List<WikiPageTitle> argument = new ArrayList<>();

        argument.addAll(nonExistTitles);
        argument.addAll(given);

        Collection<WikiPageTitle> result = titleExistenceChecker.filterExistingTitles(argument);

        // then
        assertThat(result)
                .describedAs("존재하지 않는 제목만 반환.")
                .containsExactlyInAnyOrderElementsOf(nonExistTitles);
    }

    @Test
    void filterExistingTitles_stream() {
        // given
        List<WikiPageTitle> nonExistTitles = Stream.generate(WikiPageFixture::aWikiPageTitle).limit(20).toList();
        List<WikiPageTitle> argument = new ArrayList<>();

        argument.addAll(nonExistTitles);
        argument.addAll(given);
        // when
        Stream<WikiPageTitle> streamResult = titleExistenceChecker.filterExistingTitles(argument.stream());
        List<WikiPageTitle> result = streamResult.toList();
        // then
        assertThat(result)
                .describedAs("존재하지 않는 제목만 반환.")
                .containsExactlyInAnyOrderElementsOf(nonExistTitles);

    }
}