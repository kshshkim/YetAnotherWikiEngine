package dev.prvt.yawiki.core.wikititle.history.application;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageActivatedEvent;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageRenamedEvent;
import dev.prvt.yawiki.common.model.TitleUpdateType;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageDeletedEvent;
import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TitleEventsHandlerTest {

    @Mock
    TitleHistoryService service;

    @InjectMocks
    TitleEventsHandler titleEventsHandler;

    WikiPageTitle givenTitle;

    @BeforeEach
    void init() {
        givenTitle = new WikiPageTitle(randString(), Namespace.NORMAL);
    }

    @Test
    void handleCreatedEvent() {
        WikiPageActivatedEvent givenEvent = new WikiPageActivatedEvent(givenTitle, LocalDateTime.now());

        // when
        titleEventsHandler.handleCreatedEvent(givenEvent);

        // then
        verify(service).append(givenTitle, TitleUpdateType.CREATED, givenEvent.timestamp());
    }

    @Test
    void handleDeletedEvent() {
        WikiPageDeletedEvent givenEvent = new WikiPageDeletedEvent(UUID.randomUUID(), UUID.randomUUID(), givenTitle, LocalDateTime.now());

        // when
        titleEventsHandler.handleDeletedEvent(givenEvent);

        // then
        verify(service).append(givenTitle, TitleUpdateType.DELETED, givenEvent.timestamp());

    }

    @Test
    @DisplayName("제목 변경 이벤트는 변경 이전 제목에 대해 삭제를 기록하고, 변경 이후 제목에 대해 생성을 기록함")
    void handleRenamedEvent() {
        WikiPageTitle beforeTitle = givenTitle;
        WikiPageTitle afterTitle = new WikiPageTitle(randString(), givenTitle.namespace());

        WikiPageRenamedEvent givenEvent = new WikiPageRenamedEvent(UUID.randomUUID(), UUID.randomUUID(), beforeTitle, afterTitle, LocalDateTime.now());

        // when
        titleEventsHandler.handleRenamedEvent(givenEvent);

        // then
        verify(service, description("변경 이전 제목이 삭제되었다고 기록해야함"))
                .append(beforeTitle, TitleUpdateType.DELETED, givenEvent.timestamp());
        verify(service, description("변경 이후 제목이 생성되었다고 기록해야함"))
                .append(afterTitle, TitleUpdateType.CREATED, givenEvent.timestamp());
    }
}