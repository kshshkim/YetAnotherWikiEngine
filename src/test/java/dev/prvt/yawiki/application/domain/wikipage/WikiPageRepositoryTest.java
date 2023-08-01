package dev.prvt.yawiki.application.domain.wikipage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import java.util.Optional;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WikiPageRepositoryTest {
    @PersistenceContext
    EntityManager em;
    @Autowired
    WikiPageRepository wikiPageRepository;

    private void assertEqualDocument(WikiPage given, WikiPage found) {
        assertThat(found.getId()).isEqualTo(given.getId());
        assertThat(found.getTitle()).isEqualTo(given.getTitle());
    }

    private void assertEqualRevision(Revision given, Revision found) {
        assertThat(found.getComment()).isEqualTo(given.getComment());
        assertThat(found.getRevVersion()).isEqualTo(given.getRevVersion());
        assertThat(found.getWikiPage().getId()).isEqualTo(given.getWikiPage().getId());
        assertThat(found.getRawContent().getId()).isEqualTo(given.getRawContent().getId());
    }

    private void assertEqualRawContent(RawContent given, RawContent found) {
        assertThat(found.getId()).isEqualTo(given.getId());
        assertThat(found.getContent()).isEqualTo(given.getContent());
    }

    @Test
    @Transactional
    void findById() {
        // given
        WikiPage givenWikiPage = WikiPage.create(randString());
        WikiPage save = wikiPageRepository.save(givenWikiPage);

        em.flush();
        em.clear();

        // when
        WikiPage foundWikiPage = wikiPageRepository.findById(givenWikiPage.getId()).orElseThrow();

        // then
        assertEqualDocument(givenWikiPage, foundWikiPage);
    }

    @Test
    @Transactional
    void findByTitle() {
        // given
        WikiPage givenWikiPage = WikiPage.create(randString());
        WikiPage save = wikiPageRepository.save(givenWikiPage);

        em.flush();
        em.clear();

        // when
        WikiPage foundWikiPage = wikiPageRepository.findByTitle(givenWikiPage.getTitle()).orElseThrow();

        // then
        assertEqualDocument(givenWikiPage, foundWikiPage);
    }

    @Test
    @Transactional
    void findByTitleWithRevisionAndRawContent() {
        // given
        WikiPage givenWikiPage = WikiPage.create(randString());
        givenWikiPage.updateDocument(randString(), randString() + randString() + randString());
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


        assertEqualDocument(givenWikiPage, foundGet);
        assertEqualRevision(givenWikiPage.getCurrentRevision(), foundGet.getCurrentRevision());
        assertEqualRawContent(givenWikiPage.getCurrentRevision().getRawContent(), foundGet.getCurrentRevision().getRawContent());
    }

}