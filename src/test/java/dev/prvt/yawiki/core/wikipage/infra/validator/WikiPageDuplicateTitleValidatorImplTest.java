package dev.prvt.yawiki.core.wikipage.infra.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageDuplicateTitleException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageJpaRepository;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aNormalWikiPage;
import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class WikiPageDuplicateTitleValidatorImplTest {

    @Autowired
    EntityManager em;

    @Autowired
    WikiPageJpaRepository wikiPageJpaRepository;

    WikiPageTitle duplicateTitle;

    WikiPageDuplicateTitleValidatorImpl duplicateTitleValidator;
    @BeforeEach
    void init() {
        duplicateTitleValidator = new WikiPageDuplicateTitleValidatorImpl(new WikiPageRepositoryImpl(wikiPageJpaRepository));

        WikiPage wikiPage = aNormalWikiPage();
        em.persist(wikiPage);
        em.flush();
        em.clear();
        duplicateTitle = wikiPage.getWikiPageTitle();
    }


    @Test
    void validate() {
        assertThatThrownBy(() -> duplicateTitleValidator.validate(duplicateTitle))
                .describedAs("중복된 제목이기 때문에 예외 발생")
                .isInstanceOf(WikiPageDuplicateTitleException.class);

        assertThatCode(() -> duplicateTitleValidator.validate(aWikiPageTitle()))
                .describedAs("중복되지 않은 제목이기 때문에 예외가 발생하지 않음.")
                .doesNotThrowAnyException();
    }
}