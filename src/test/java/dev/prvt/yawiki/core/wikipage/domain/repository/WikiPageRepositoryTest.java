package dev.prvt.yawiki.core.wikipage.domain.repository;

import dev.prvt.yawiki.core.wikipage.domain.model.RawContent;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.Fixture.*;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WikiPageRepositoryTest {
    @PersistenceContext
    EntityManager em;
    @Autowired
    WikiPageRepository wikiPageRepository;
    WikiPage testWikiPage;

    @BeforeEach
    void initGivenWikiPage() {
        testWikiPage = WikiPage.create(randString());
        testWikiPage = wikiPageRepository.save(testWikiPage);
        em.flush();
        em.clear();
    }

    @Test
    void findById() {
        // given

        // when
        WikiPage foundWikiPage = wikiPageRepository.findById(testWikiPage.getId()).orElseThrow();

        // then
        assertEqualWikiPage(foundWikiPage, testWikiPage);
    }

    @Test
    void findByTitle() {
        // given

        // when
        WikiPage foundWikiPage = wikiPageRepository.findByTitle(testWikiPage.getTitle()).orElseThrow();

        // then
        assertEqualWikiPage(foundWikiPage, testWikiPage);
    }

    @Test
    void findByTitleWithRevisionAndRawContent() {
        // given
        WikiPage givenWikiPage = WikiPage.create(randString());
        givenWikiPage.update(UUID.randomUUID(), randString(), randString() + randString() + randString());
        wikiPageRepository.save(givenWikiPage);

        em.flush();
        em.clear();
        // when
        Optional<WikiPage> found = wikiPageRepository.findByTitleWithRevisionAndRawContent(givenWikiPage.getTitle());

        // then
        assertThat(found).isPresent();
        WikiPage foundGet = found.get();
        PersistenceUnitUtil persistenceUnitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();

        assertThat(foundGet.getCurrentRevision())
                .describedAs("fetch join 이 정상 작동해야함.")
                .satisfies(persistenceUnitUtil::isLoaded);
        assertThat(foundGet.getCurrentRevision().getRawContent())
                .describedAs("fetch join 이 정상 작동해야함.")
                .satisfies(persistenceUnitUtil::isLoaded);


        assertEqualWikiPage(foundGet, givenWikiPage);
        assertEqualRevision(foundGet.getCurrentRevision(), givenWikiPage.getCurrentRevision());
        assertEqualRawContent(foundGet.getCurrentRevision().getRawContent(), givenWikiPage.getCurrentRevision().getRawContent());
    }

    @Test
    void ids_should_be_generated_when_flush_aggregate_root() {
        // given
        WikiPage givenWikiPage = wikiPageRepository.findById(testWikiPage.getId())
                .orElseThrow();
        updateWikiPageRandomly(givenWikiPage);
        Revision givenRevision = givenWikiPage.getCurrentRevision();
        RawContent givenRawContent = givenRevision.getRawContent();

        // when
        em.flush();

        // then
        assertThat(givenWikiPage.getId())
                .isNotNull();
        assertThat(givenRevision.getId())
                .isNotNull();
        assertThat(givenRawContent.getId())
                .isNotNull();
    }

    @Test
    void version_should_be_updated() {
        // given
        WikiPage givenWikiPage = wikiPageRepository.findById(testWikiPage.getId())
                .orElseThrow();
        updateWikiPageRandomly(givenWikiPage);

        // when
        em.flush();

        int oldVersion = testWikiPage.getVersion();
        int updatedVersion = givenWikiPage.getVersion();

        // then
        assertThat(updatedVersion)
                .isGreaterThan(oldVersion);
    }
}