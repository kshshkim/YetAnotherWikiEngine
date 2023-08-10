package dev.prvt.yawiki.core.wikipage.infra.repository;

import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.core.wikireference.domain.WikiReference;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceRepository;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiReferenceRepositoryImpl;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikireference.infra.WikiReferenceJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class WikiReferenceRepositoryImplTest {

    @Autowired
    EntityManager em;

    @Autowired
    WikiReferenceRepositoryImpl wikiReferenceRepository;

    private WikiPage givenWikiPage;

    private List<String> givenRefTitles;

    @BeforeEach
    void initData() {
        givenWikiPage = WikiPage.create(randString());
        em.persist(givenWikiPage);
        log.info("persisted");
        List<WikiReference> refs = IntStream.range(0, 10)
                .mapToObj(i -> UUID.randomUUID().toString())
                .map(title -> new WikiReference(givenWikiPage.getId(), title))
                .toList();
        givenRefTitles = refs.stream()
                .map(WikiReference::getReferredTitle)
                .toList();

        wikiReferenceRepository.saveAll(refs);


        em.flush();
        em.clear();
    }

    @Test
    void findReferredTitlesByRefererId() {
        // when
        Set<String> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found).containsAll(givenRefTitles);
        assertThat(found).hasSize(givenRefTitles.size());
    }

    @Test
    void should_find_nothing_when_document_does_not_exists() {
        // when
        Set<String> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());
        // then
        assertThat(found).isEmpty();
    }


    @Test
    void should_find_nothing_when_document_is_not_active() {
        // given
        givenRefTitles.stream()
                .limit(3)
                .map(WikiPage::create)
                .forEach(em::persist);

        em.flush();
        em.clear();

        // when
        Set<String> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void should_find_only_active_wiki_pages() {
        // given
        List<WikiPage> createdWikiPage = givenRefTitles.stream()
                .limit(3)
                .map(WikiPage::create)
                .toList();

        for (WikiPage wikiPage : createdWikiPage) {
            wikiPage.update(UUID.randomUUID(), randString(), randString());
            em.persist(wikiPage);
        }
        em.flush();
        em.clear();

        List<String> activeWikiTitles = createdWikiPage.stream()
                .map(WikiPage::getTitle)
                .toList();

        // when
        Set<String> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(activeWikiTitles);
    }

    @Test
    void delete() {
        // given
        List<String> toDelete = givenRefTitles.subList(0, 1);

        // when
        wikiReferenceRepository.delete(givenWikiPage.getId(), toDelete);

        // then
        Set<String> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .describedAs("toDelete 에 포함된 요소가 제거되어야함.")
                .doesNotContainAnyElementsOf(toDelete);
    }

    @Test
    void deleteExcept() {
        // given
        List<String> notToDelete = givenRefTitles.subList(0, 1);

        // when
        wikiReferenceRepository.deleteExcept(givenWikiPage.getId(), notToDelete);

        // then
        Set<String> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .describedAs("notToDelete 에 포함된 요소만 남아야함.")
                .containsExactlyInAnyOrderElementsOf(notToDelete);
    }

    @Test
    void bulkInsert() {
        // given
        UUID givenRefererId = UUID.randomUUID();

        // when
        wikiReferenceRepository.bulkInsert(givenRefererId, givenRefTitles);

        // then
        Set<String> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenRefererId);
        assertThat(found)
                .containsExactlyInAnyOrderElementsOf(givenRefTitles);
    }
}