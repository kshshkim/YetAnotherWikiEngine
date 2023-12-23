package dev.prvt.yawiki.core.wikipage.infra.event;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageEventFactory;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageEventPublisher;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Set;

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

    @Override
    public void updateCommitted(WikiPage wikiPage, Set<WikiPageTitle> referencedTitles) {
        eventPublisher.publishEvent(eventFactory.wikiPageUpdateCommittedEvent(wikiPage, referencedTitles));
        if (wikiPage.isActivated()) {
            this.activated(wikiPage);
        }
    }
}
