package dev.prvt.yawiki.titleexistence.cache.application;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.testutil.CommonFixture;
import dev.prvt.yawiki.titleexistence.cache.domain.CacheStorage;
import dev.prvt.yawiki.titleexistence.cache.domain.InitialCacheData;
import dev.prvt.yawiki.titleexistence.cache.infra.CacheStorageConcurrentHashMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WikiPageTitleExistenceCheckerImplTest {

    CacheStorage cacheStorage;

    WikiPageTitleExistenceFilter titleExistenceChecker;

    List<WikiPageTitle> given = Stream.generate(CommonFixture::aWikiPageTitle).limit(20).toList();

    @BeforeEach
    void init() {
        cacheStorage = new CacheStorageConcurrentHashMapImpl();
        cacheStorage.init(new InitialCacheData<>(given.stream(), given.size(), LocalDateTime.now()));
        titleExistenceChecker = new WikiPageTitleExistenceFilter(cacheStorage);
    }

    @Test
    @DisplayName("존재하는 제목과 존재하지 않는 제목을 이 포함된 파라미터를 넘기는 경우, 존재하지 않는 제목만 반환해야함.")
    void filterExistingTitles_collection() {
        // when
        List<WikiPageTitle> nonExistTitles = Stream.generate(CommonFixture::aWikiPageTitle).limit(20).toList();
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