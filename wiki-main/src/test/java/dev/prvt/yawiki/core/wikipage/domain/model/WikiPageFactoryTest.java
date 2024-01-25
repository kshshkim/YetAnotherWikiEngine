package dev.prvt.yawiki.core.wikipage.domain.model;

import dev.prvt.yawiki.common.model.WikiPageTitle;
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
                .describedAs("문서 제목이 적절히 설정되어야함.")
                .isEqualTo(givenWikiPageTitle.title());

        assertThat(wikiPage.getNamespace())
                .describedAs("네임스페이스가 적절히 설정되어야함.")
                .isEqualTo(givenWikiPageTitle.namespace());

        assertThat(wikiPage.getVersionToken())
                .describedAs("생성시 버전토큰도 생성되어야함.")
                .isNotNull()
                .isNotBlank();

        assertThat(wikiPage.getLastModifiedAt())
                .describedAs("생성시에도 마지막 변경 시간이 설정됨.")
                .isNotNull();

        assertThat(wikiPage.getLastModifiedBy())
                .describedAs("생성 시점에 contributorId를 지정하지 않은 경우 null")
                .isNull();
    }

    @Test
    void create_with_contributorId() {
        UUID givenContributor = UUID.randomUUID();

        WikiPage wikiPage = wikiPageFactory.create(givenWikiPageTitle.title(), givenWikiPageTitle.namespace(), givenContributor);

        assertThat(wikiPage.getTitle())
                .describedAs("문서 제목이 적절히 설정되어야함.")
                .isEqualTo(givenWikiPageTitle.title());

        assertThat(wikiPage.getNamespace())
                .describedAs("네임스페이스가 적절히 설정되어야함.")
                .isEqualTo(givenWikiPageTitle.namespace());

        assertThat(wikiPage.getVersionToken())
                .describedAs("생성시 버전토큰도 생성되어야함.")
                .isNotNull()
                .isNotBlank();

        assertThat(wikiPage.getLastModifiedAt())
                .describedAs("생성시에도 마지막 변경 시간이 설정됨.")
                .isNotNull();

        assertThat(wikiPage.getLastModifiedBy())
                .describedAs("생성 시점에 contributorId를 지정하지 않은 경우 null")
                .isEqualTo(givenContributor);
    }

    @Test
    void create_active_should_false_when_just_created() {
        WikiPage wikiPage = wikiPageFactory.create(givenWikiPageTitle.title(), givenWikiPageTitle.namespace());

        assertThat(wikiPage.isActive())
                .isFalse();
        assertThat(wikiPage.isActivated())
                .isFalse();
    }
}