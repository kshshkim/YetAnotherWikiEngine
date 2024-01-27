package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static dev.prvt.yawiki.fixture.WikiPageFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class WikiPageEventFactoryTest {

    private final WikiPageEventFactory eventFactory = new WikiPageEventFactory();

    WikiPage givenWikiPage;

    @SneakyThrows
    @BeforeEach
    void init() {
        givenWikiPage = aNormalWikiPage();
        setWikiPageId(givenWikiPage, UUID.randomUUID());
        setWikiPageLastModifiedBy(givenWikiPage, UUID.randomUUID());
    }

    @Test
    void wikiPageActivatedEvent() {
        WikiPageActivatedEvent event = eventFactory.wikiPageActivatedEvent(givenWikiPage);

        assertThat(event.wikiPageTitle())
                .isEqualTo(givenWikiPage.getWikiPageTitle());

        assertThat(event.timestamp())
                .isEqualTo(givenWikiPage.getLastModifiedAt());
    }

    @Test
    void wikiPageCreatedEvent() {
        WikiPageCreatedEvent event = eventFactory.wikiPageCreatedEvent(givenWikiPage);

        assertThat(event.id())
                .isEqualTo(givenWikiPage.getId());
        assertThat(event.wikiPageTitle())
                .isEqualTo(givenWikiPage.getWikiPageTitle());
        assertThat(event.timestamp())
                .isEqualTo(givenWikiPage.getLastModifiedAt());
    }

    @Test
    void wikiPageDeletedEvent() {
        WikiPageDeletedEvent event = eventFactory.wikiPageDeletedEvent(givenWikiPage);

        assertThat(event.deletedTitle())
                .isEqualTo(givenWikiPage.getWikiPageTitle());

        assertThat(event.timestamp())
                .isEqualTo(givenWikiPage.getLastModifiedAt());

        assertThat(event.contributorId())
                .isEqualTo(givenWikiPage.getLastModifiedBy());
    }

    @Test
    void wikiPageUpdateCommittedEvent() {
        // given
        Set<WikiPageTitle> referencedTitles = Stream.generate(WikiPageFixture::aWikiPageTitle)
                .limit(20)
                .collect(Collectors.toSet());

        // when
        WikiPageUpdateCommittedEvent event = eventFactory.wikiPageUpdateCommittedEvent(givenWikiPage, referencedTitles);

        // then
        assertThat(List.of(event.wikiPageTitle(), event.wikiPageId(), event.contributorId()))
                .containsExactly(givenWikiPage.getWikiPageTitle(), givenWikiPage.getId(), givenWikiPage.getLastModifiedBy());

        assertThat(event.referencedTitles())
                .containsExactlyInAnyOrderElementsOf(referencedTitles);
    }

    @Test
    void wikiPageRenamedEvent() {
        // given
        updateWikiPageRandomly(givenWikiPage);
        WikiPageTitle beforeTitle = givenWikiPage.getWikiPageTitle();
        WikiPageTitle newTitle = new WikiPageTitle(randString(), beforeTitle.namespace());
        UUID contributorId = UUID.randomUUID();
        givenWikiPage.rename(contributorId, newTitle.title(), randString());

        // when
        WikiPageRenamedEvent event = eventFactory.wikiPageRenamedEvent(givenWikiPage, beforeTitle);

        // then
        assertThat(event.timestamp())
                .isNotNull()
                .isEqualTo(givenWikiPage.getLastModifiedAt());

        assertThat(event.wikiPageId())
                .isNotNull()
                .isEqualTo(givenWikiPage.getId());

        assertThat(event.contributorId())
                .isNotNull()
                .isEqualTo(contributorId);

        assertThat(event.beforeTitle())
                .isNotNull()
                .isEqualTo(beforeTitle);

        assertThat(event.afterTitle())
                .isNotNull()
                .isEqualTo(newTitle);

    }
}