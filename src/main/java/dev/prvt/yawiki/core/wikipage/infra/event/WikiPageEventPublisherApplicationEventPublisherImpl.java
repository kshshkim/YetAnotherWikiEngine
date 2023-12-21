package dev.prvt.yawiki.core.wikipage.infra.event;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageEventFactory;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageEventPublisher;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WikiPageEventPublisherApplicationEventPublisherImpl implements WikiPageEventPublisher {
    private final ApplicationEventPublisher eventPublisher;
    private final WikiPageEventFactory eventFactory;

    @Override
    public void created(WikiPage wikiPage) {
        eventPublisher.publishEvent(eventFactory.wikiPageCreatedEvent(wikiPage));
    }

    @Override
    public void activated(WikiPage wikiPage) {
        eventPublisher.publishEvent(eventFactory.wikiPageActivatedEvent(wikiPage));
    }

    @Override
    public void deactivated(WikiPage wikiPage) {
        eventPublisher.publishEvent(eventFactory.wikiPageDeletedEvent(wikiPage));
    }
}
