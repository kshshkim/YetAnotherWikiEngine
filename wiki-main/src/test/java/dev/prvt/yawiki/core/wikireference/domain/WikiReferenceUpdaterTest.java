package dev.prvt.yawiki.core.wikireference.domain;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class WikiReferenceUpdaterTest {

    @Autowired
    WikiReferenceUpdater updater;

    @Autowired
    WikiReferenceRepository wikiReferenceRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("기존 레퍼런스가 존재하지 않는 상황에서 새로운 레퍼런스를 추가하는 상황 테스트")
    void updateReferences_add_new_references_1() {
        // given
        UUID refererId = UUID.randomUUID();
        Set<WikiPageTitle> references = generateReferences(10);

        // when
        updater.updateReferences(refererId, references);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(refererId);
        assertThat(found)
                .describedAs("추가된 레퍼런스만 존재해야함.")
                .containsExactlyInAnyOrderElementsOf(references);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 8, 10})
    @DisplayName("파라미터 만큼의 기존 레퍼런스를 남기고, 3개의 새 레퍼런스를 추가하는 상황 테스트")
    void updateReferences_update_references_2(int count) {
        // given
        UUID refererId = UUID.randomUUID();
        Set<WikiPageTitle> originalReferences = generateReferences(10);
        updater.updateReferences(refererId, originalReferences);

        Set<WikiPageTitle> remainingReferences = originalReferences.stream().limit(count).collect(Collectors.toSet());
        Set<WikiPageTitle> addedReferences = generateReferences(3);

        Set<WikiPageTitle> updatedReferences = union(remainingReferences, addedReferences);
        // when
        updater.updateReferences(refererId, updatedReferences);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(refererId);
        assertThat(found)
                .describedAs("남아있는 레퍼런스가 모두 존재해야함.")
                .containsAll(remainingReferences)
                .describedAs("새로 추가된 레퍼런스가 모두 존재해야함.")
                .containsAll(addedReferences)
                .describedAs("updated 레퍼런스만 존재해야함.")
                .containsExactlyInAnyOrderElementsOf(updatedReferences)
        ;
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 8, 10})
    @DisplayName("파라미터 만큼의 기존 레퍼런스를 남기고, 아무런 새 레퍼런스를 추가하지 않는 상황 테스트")
    void updateReferences_only_delete(int count) {
        // given
        UUID refererId = UUID.randomUUID();
        Set<WikiPageTitle> originalReferences = generateReferences(10);
        updater.updateReferences(refererId, originalReferences);

        Set<WikiPageTitle> remainingReferences = originalReferences.stream().limit(count).collect(Collectors.toSet());

        // when
        updater.updateReferences(refererId, remainingReferences);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(refererId);
        assertThat(found)
                .describedAs("남아있는 레퍼런스만 존재해야함.")
                .containsExactlyInAnyOrderElementsOf(remainingReferences)
        ;
    }

    @Test
    @DisplayName("모든 레퍼런스를 지우는 상황 테스트")
    void deleteReferences() {
        // given
        UUID refererId = UUID.randomUUID();
        Set<WikiPageTitle> originalReferences = generateReferences(10);
        updater.updateReferences(refererId, originalReferences);

        // when
        updater.deleteReferences(refererId);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(refererId);
        assertThat(found)
                .isEmpty();
    }


    @NotNull
    private static Set<WikiPageTitle> generateReferences(int howMany) {
        return Stream.generate(WikiPageFixture::aWikiPageTitle)
                .limit(howMany)
                .collect(Collectors.toUnmodifiableSet());
    }

    Set<WikiPageTitle> union(Set<WikiPageTitle> set1, Set<WikiPageTitle> set2) {
        Set<WikiPageTitle> temp = new HashSet<>();
        temp.addAll(set1);
        temp.addAll(set2);
        return temp;
    }

}