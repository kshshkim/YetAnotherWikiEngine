package dev.prvt.yawiki.core.wikireference.application;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageDeletedEvent;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageUpdateCommittedEvent;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WikiPageUpdatedEventListener {
    private final WikiReferenceUpdater wikiReferenceUpdater;

    @Transactional
    @EventListener
    public void handle(WikiPageUpdateCommittedEvent event) {
        wikiReferenceUpdater.updateReferences(event.wikiPageId(), event.referencedTitles());
    }

    @Transactional
    @EventListener
    public void handle(WikiPageDeletedEvent event) {
        wikiReferenceUpdater.deleteReferences(event.wikiPageId());
    }
}
