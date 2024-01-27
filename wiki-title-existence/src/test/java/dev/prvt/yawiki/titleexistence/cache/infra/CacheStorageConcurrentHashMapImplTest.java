package dev.prvt.yawiki.titleexistence.cache.infra;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.titleexistence.cache.domain.InitialCacheData;
import dev.prvt.yawiki.common.util.test.CommonFixture;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static dev.prvt.yawiki.common.util.test.CommonFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CacheStorageConcurrentHashMapImplTest {

    CacheStorageConcurrentHashMapImpl storage;

    WikiPageTitle givenElement;

    List<WikiPageTitle> givenElements;
    long maxCount = 100L;

    @BeforeEach
    void test_before_each() {
        storage = new CacheStorageConcurrentHashMapImpl();
        InitialCacheData<WikiPageTitle> givenInitialData = new InitialCacheData<>(Stream.empty(), 0, LocalDateTime.MIN);
        storage.init(givenInitialData);
        givenElement = aWikiPageTitle();
        givenElements = Stream
                .generate(
                        CommonFixture::aWikiPageTitle
                )
                .limit(maxCount)
                .toList();
        System.out.println();
    }

    @Test
    void add() {
        // given

        // when
        storage.add(givenElement);

        // then
        storage.exists(givenElement);
    }

    @Test
    void add_duplicate() {
        // given
        storage.add(givenElement);
        WikiPageTitle duplicate = new WikiPageTitle(givenElement.title(), givenElement.namespace());

        // when then
        assertThatCode(() -> storage.add(duplicate))
                .describedAs("중복 원소를 삽입해도 예외가 발생하지 않아야함.")
                .doesNotThrowAnyException();
    }

    @Test
    void addAll() {
        // given

        // when
        storage.addAll(givenElements);

        // then
        assertThat(givenElements)
                .describedAs("모두 add 되어 존재해야함.")
                .allSatisfy(storage::exists);
    }

    @Test
    void remove() {
        // given
        storage.add(givenElement);

        // when
        storage.remove(givenElement);

        // then
        assertThat(storage.exists(givenElement))
                .describedAs("지워졌기 때문에 존재하지 않음.")
                .isFalse();
    }

    @Test
    void remove_non_existent() {
        // given
        storage.add(givenElement);
        storage.remove(givenElement);

        // when then
        assertThatCode(() -> storage.remove(givenElement))
                .describedAs("없는 제목을 제거하더라도 예외가 발생하지 않음.")
                .doesNotThrowAnyException();

        assertThat(storage.exists(givenElement))
                .describedAs("지워졌기 때문에 존재하지 않음.")
                .isFalse();
    }

    @Test
    void filterExistentTitles() {
        // given
        storage.addAll(givenElements);
        List<WikiPageTitle> nonExistentTitles = Stream.generate(CommonFixture::aWikiPageTitle)
                                       .limit(10)
                                       .toList();
        List<WikiPageTitle> argument = new ArrayList<>(nonExistentTitles);
        argument.addAll(givenElements);  // 존재하지 않는 제목과 존재하는 제목 모두 포함

        // when
        Collection<WikiPageTitle> result = storage.filterExistentTitles(argument);


        // then
        assertThat(result)
            .describedAs("존재하지 않는 제목만 포함되어야함.")
            .containsExactlyInAnyOrderElementsOf(nonExistentTitles);

    }

    @Test
    void filterExistentTitles_Stream_param() {
        // given
        storage.addAll(givenElements);
        List<WikiPageTitle> nonExistentTitles = Stream.generate(CommonFixture::aWikiPageTitle)
                                       .limit(10)
                                       .toList();
        List<WikiPageTitle> argument = new ArrayList<>(nonExistentTitles);
        argument.addAll(givenElements);  // 존재하지 않는 제목과 존재하는 제목 모두 포함

        // when
        Collection<WikiPageTitle> result = storage.filterExistentTitles(argument.stream());


        // then
        assertThat(result)
            .describedAs("존재하지 않는 제목만 포함되어야함.")
            .containsExactlyInAnyOrderElementsOf(nonExistentTitles);

    }

    @Test
    void removeAll() {
        // given
        List<WikiPageTitle> given = Stream.generate(CommonFixture::aWikiPageTitle)
                .limit(maxCount)
                .toList();
        storage.addAll(given);

        // when
        storage.removeAll(given);

        // then
        assertThat(given)
                .describedAs("모두 존재함.")
                .allMatch(wpt -> !storage.exists(wpt));
    }

    @Test
    void init() {
        // given
        List<WikiPageTitle> initialData = Stream.generate(CommonFixture::aWikiPageTitle).limit(maxCount).toList();

        InitialCacheData<WikiPageTitle> initialCacheData = new InitialCacheData<>(
                initialData.stream(),
                Long.valueOf(maxCount).intValue(),
                LocalDateTime.now()
        );

        // when
        storage.init(initialCacheData);

        // then
        assertThat(initialData)
                .describedAs("모두 존재함.")
                .allMatch(storage::exists);
    }
}