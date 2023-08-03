package dev.prvt.yawiki.app.wikipage;

import dev.prvt.yawiki.app.wikipage.domain.WikiPage;
import dev.prvt.yawiki.app.wikipage.domain.WikiPageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import java.util.Optional;

import static dev.prvt.yawiki.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WikiPageRepositoryTest {
    @PersistenceContext
    EntityManager em;
    @Autowired
    WikiPageRepository wikiPageRepository;

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
        assertEqualWikiPage(foundWikiPage, givenWikiPage);
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
        assertEqualWikiPage(foundWikiPage, givenWikiPage);
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


        assertEqualWikiPage(foundGet, givenWikiPage);
        assertEqualRevision(foundGet.getCurrentRevision(), givenWikiPage.getCurrentRevision());
        assertEqualRawContent(foundGet.getCurrentRevision().getRawContent(), givenWikiPage.getCurrentRevision().getRawContent());
    }

}