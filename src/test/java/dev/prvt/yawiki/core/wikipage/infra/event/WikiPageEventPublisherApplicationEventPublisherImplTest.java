package dev.prvt.yawiki.core.wikipage.infra.event;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageActivatedEvent;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageCreatedEvent;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageDeletedEvent;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageEventFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.infra.event.WikiPageEventPublisherApplicationEventPublisherImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WikiPageEventPublisherApplicationEventPublisherImplTest {

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Mock
    WikiPageEventFactory wikiPageEventFactory;

    @InjectMocks
    WikiPageEventPublisherApplicationEventPublisherImpl wikiPageEventPublisher;

    @Mock
    WikiPage mockWikiPage;

    @Test
    void created() {
        // given
        WikiPageCreatedEvent event = new WikiPageCreatedEvent(UUID.randomUUID(), aWikiPageTitle(), LocalDateTime.now());
        given(wikiPageEventFactory.wikiPageCreatedEvent(mockWikiPage))
                .willReturn(event);

        // when
        wikiPageEventPublisher.created(mockWikiPage);

        // then
        verify(applicationEventPublisher).publishEvent(event);
    }

    @Test
    void activated() {
        // given
        WikiPageActivatedEvent event = new WikiPageActivatedEvent(aWikiPageTitle(), LocalDateTime.now());
        given(wikiPageEventFactory.wikiPageActivatedEvent(mockWikiPage))
                .willReturn(event);

        // when
        wikiPageEventPublisher.activated(mockWikiPage);

        // then
        verify(applicationEventPublisher).publishEvent(event);

    }

    @Test
    void deactivated() {
        // given
        WikiPageDeletedEvent event = new WikiPageDeletedEvent(UUID.randomUUID(), UUID.randomUUID(), aWikiPageTitle(), LocalDateTime.now());
        given(wikiPageEventFactory.wikiPageDeletedEvent(mockWikiPage))
                .willReturn(event);

        // when
        wikiPageEventPublisher.deactivated(mockWikiPage);

        // then
        verify(applicationEventPublisher).publishEvent(event);

    }
}