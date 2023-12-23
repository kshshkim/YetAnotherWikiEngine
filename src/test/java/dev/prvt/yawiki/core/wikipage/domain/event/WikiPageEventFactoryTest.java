package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.WikiPageFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
}