package dev.prvt.yawiki.application.domain.innerreference;

import dev.prvt.yawiki.application.domain.wikipage.WikiPage;
import org.junit.jupiter.api.BeforeEach;
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

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class WikiPageUpdatedReferenceServiceImplTest {

    @Autowired
    WikiPageUpdatedReferenceServiceImpl service;

    @Autowired
    InnerReferenceRepository innerReferenceRepository;

    @Autowired
    EntityManager em;

    private WikiPage givenWikiPage;
    private Set<String> givenRefTitles;

    @BeforeEach
    void initData() {
        givenWikiPage = WikiPage.create(randString());
        em.persist(givenWikiPage);

        List<InnerReference> givenReferences = IntStream.range(0, 10)
                .mapToObj(i -> randString())
                .map(title -> new InnerReference(givenWikiPage.getId(), title))
                .toList();

        givenRefTitles = givenReferences.stream()
                .map(InnerReference::getReferredTitle)
                .collect(Collectors.toSet());

        innerReferenceRepository.saveAll(givenReferences);

        em.flush();
        em.clear();
    }

    @Test
    void createRefs() {
        // given
        Set<String> newRefs = Set.of(randString(), randString(), randString());
        Set<String> updatedRefs = new HashSet<>(givenRefTitles);
        updatedRefs.addAll(newRefs);

        // when
        service.createRefs(givenWikiPage.getId(), givenRefTitles, updatedRefs);

        // then
        Set<String> found = innerReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());

        assertThat(found)
                .containsAll(newRefs);
    }

    @Test
    void deleteRefs_deleteSmall() {
        // given
        Set<String> updatedRefs = givenRefTitles.stream()
                .limit(8L)
                .collect(Collectors.toSet());

        // when
        service.deleteRefs(givenWikiPage.getId(), givenRefTitles, updatedRefs);

        // then
        Set<String> found = innerReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .containsExactlyInAnyOrderElementsOf(updatedRefs);
    }

    @Test
    void deleteRefs_deleteLarge() {
        // given
        Set<String> updatedRefs = givenRefTitles.stream()
                .limit(2L)
                .collect(Collectors.toSet());

        // when
        service.deleteRefs(givenWikiPage.getId(), givenRefTitles, updatedRefs);

        // then
        Set<String> found = innerReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .containsExactlyInAnyOrderElementsOf(updatedRefs);
    }

    @Test
    void updateReferences() {
        // given
        Set<String> newRefs = Set.of(randString(), randString(), randString());
        Set<String> updatedRefs = givenRefTitles.stream()
                .limit(8L)
                .collect(Collectors.toSet());
        updatedRefs.addAll(newRefs);

        // when
        service.updateReferences(givenWikiPage.getId(), updatedRefs);

        // then
        Set<String> found = innerReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .containsExactlyInAnyOrderElementsOf(updatedRefs);
    }
}