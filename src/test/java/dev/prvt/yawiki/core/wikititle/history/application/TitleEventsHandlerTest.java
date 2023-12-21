package dev.prvt.yawiki.core.wikititle.history.application;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageActivatedEvent;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleUpdateType;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageDeletedEvent;
import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TitleEventsHandlerTest {

    @Mock
    TitleHistoryService service;

    @InjectMocks
    TitleEventsHandler titleEventsHandler;

    @Test
    void handleCreatedEvent() {
        WikiPageTitle givenTitle = new WikiPageTitle(randString(), Namespace.NORMAL);
        WikiPageActivatedEvent givenEvent = new WikiPageActivatedEvent(givenTitle, LocalDateTime.now());

        // when
        titleEventsHandler.handleCreatedEvent(givenEvent);

        // then
        verify(service).append(givenTitle, TitleUpdateType.CREATED, givenEvent.timestamp());
    }

    @Test
    void handleDeletedEvent() {
        WikiPageTitle givenTitle = new WikiPageTitle(randString(), Namespace.NORMAL);
        WikiPageDeletedEvent givenEvent = new WikiPageDeletedEvent(UUID.randomUUID(), UUID.randomUUID(), givenTitle, LocalDateTime.now());

        // when
        titleEventsHandler.handleDeletedEvent(givenEvent);

        // then
        verify(service).append(givenTitle, TitleUpdateType.DELETED, givenEvent.timestamp());

    }
}