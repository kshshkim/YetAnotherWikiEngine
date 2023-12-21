package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aNormalWikiPage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WikiPageEventFactoryTest {

    private final WikiPageEventFactory eventFactory = new WikiPageEventFactory();

    WikiPage givenWikiPage;

    @SneakyThrows
    @BeforeEach
    void init() {
        givenWikiPage = aNormalWikiPage();
        Field idField = givenWikiPage.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(givenWikiPage, UUID.randomUUID());

        Field lastModifiedBy = givenWikiPage.getClass().getDeclaredField("lastModifiedBy");
        lastModifiedBy.setAccessible(true);
        lastModifiedBy.set(givenWikiPage, UUID.randomUUID());

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
}