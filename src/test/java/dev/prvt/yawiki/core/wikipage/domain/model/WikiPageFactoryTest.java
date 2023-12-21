package dev.prvt.yawiki.core.wikipage.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThat;

class WikiPageFactoryTest {

    private final WikiPageFactory wikiPageFactory = new WikiPageFactory();

    private WikiPageTitle givenWikiPageTitle;

    @BeforeEach
    void init() {
        givenWikiPageTitle = aWikiPageTitle();
    }

    @Test
    void create_without_contributorId() {
        WikiPage wikiPage = wikiPageFactory.create(givenWikiPageTitle.title(), givenWikiPageTitle.namespace());

        assertThat(wikiPage.getTitle())
                .isEqualTo(givenWikiPageTitle.title());

        assertThat(wikiPage.getNamespace())
                .isEqualTo(givenWikiPageTitle.namespace());

        assertThat(wikiPage.getVersionToken())
                .isNotNull();

        assertThat(wikiPage.getLastModifiedAt())
                .isNotNull();

        assertThat(wikiPage.getLastModifiedBy())
                .isNull();
    }

    @Test
    void create_with_contributorId() {
        UUID givenContributor = UUID.randomUUID();

        WikiPage wikiPage = wikiPageFactory.create(givenWikiPageTitle.title(), givenWikiPageTitle.namespace(), givenContributor);

        assertThat(wikiPage.getTitle())
                .isEqualTo(givenWikiPageTitle.title());

        assertThat(wikiPage.getNamespace())
                .isEqualTo(givenWikiPageTitle.namespace());

        assertThat(wikiPage.getVersionToken())
                .isNotNull();

        assertThat(wikiPage.getLastModifiedAt())
                .describedAs("생성시에도 마지막 변경 시간이 설정됨.")
                .isNotNull();

        assertThat(wikiPage.getLastModifiedBy())
                .describedAs("마지막 수정자가 기입됨.")
                .isEqualTo(givenContributor);
    }
}