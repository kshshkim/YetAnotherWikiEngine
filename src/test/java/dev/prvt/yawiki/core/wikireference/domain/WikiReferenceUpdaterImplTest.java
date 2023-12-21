package dev.prvt.yawiki.core.wikireference.domain;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static dev.prvt.yawiki.fixture.WikiPageFixture.aNormalWikiPage;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class WikiReferenceUpdaterImplTest {

    @Autowired
    WikiReferenceUpdaterImpl service;

    @Autowired
    WikiReferenceRepository wikiReferenceRepository;

    @Autowired
    EntityManager em;

    private WikiPage givenWikiPage;
    private Set<WikiPageTitle> givenRefTitles;

    @BeforeEach
    void initData() {
        givenWikiPage = aNormalWikiPage();
        em.persist(givenWikiPage);

        List<WikiReference> givenReferences = IntStream.range(0, 10)
                .mapToObj(i -> randString())
                .map(title -> new WikiReference(givenWikiPage.getId(), title, Namespace.NORMAL))
                .toList();

        givenRefTitles = givenReferences.stream()
                .map(wr -> new WikiPageTitle(wr.getReferredTitle(), wr.getNamespace()))
                .collect(Collectors.toSet());

        wikiReferenceRepository.saveAll(givenReferences);

        em.flush();
        em.clear();
    }

    @Test
    void createRefs() {
        // given
        Set<WikiPageTitle> newRefs = Set.of(
                new WikiPageTitle(randString(), Namespace.NORMAL),
                new WikiPageTitle(randString(), Namespace.NORMAL),
                new WikiPageTitle(randString(), Namespace.NORMAL)
        );
        Set<WikiPageTitle> updatedRefs = new HashSet<>(givenRefTitles);
        updatedRefs.addAll(newRefs);

        // when
        service.createRefs(givenWikiPage.getId(), givenRefTitles, updatedRefs);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());

        assertThat(found)
                .containsAll(newRefs);
    }

    @Test
    @DisplayName("총 10개의 레퍼런스가 업데이트되어, 8개 남은 상황")
    void deleteRefs_deleteSmall() {
        // given
        Set<WikiPageTitle> updatedRefs = givenRefTitles.stream()
                .limit(8L)
                .collect(Collectors.toSet());

        // when
        service.deleteRefs(givenWikiPage.getId(), givenRefTitles, updatedRefs);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .containsExactlyInAnyOrderElementsOf(updatedRefs);
    }

    @Test
    @DisplayName("총 10개의 레퍼런스가 업데이트되어, 2개 남은 상황")
    void deleteRefs_deleteLarge() {
        // given
        Set<WikiPageTitle> updatedRefs = givenRefTitles.stream()
                .limit(2L)
                .collect(Collectors.toSet());

        // when
        service.deleteRefs(givenWikiPage.getId(), givenRefTitles, updatedRefs);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .containsExactlyInAnyOrderElementsOf(updatedRefs);
    }

    @Test
    void updateReferences() {
        // given
        Set<WikiPageTitle> newRefs = Set.of(
                new WikiPageTitle(randString(), Namespace.NORMAL),
                new WikiPageTitle(randString(), Namespace.NORMAL),
                new WikiPageTitle(randString(), Namespace.NORMAL)
        );
        Set<WikiPageTitle> updatedRefs = givenRefTitles.stream()
                .limit(8L)
                .collect(Collectors.toSet());
        updatedRefs.addAll(newRefs);

        // when
        service.updateReferences(givenWikiPage.getId(), updatedRefs);
        em.flush();
        em.clear();
        System.out.println("givenWikiPage = " + givenWikiPage.getId());
        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .containsExactlyInAnyOrderElementsOf(updatedRefs);
    }

    @Test
    void deleteReferences() {
        // when
        service.deleteReferences(givenWikiPage.getId());

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .isEmpty();
    }
}