package dev.prvt.yawiki.core.wikititle.history.application;

import dev.prvt.yawiki.core.wikititle.history.domain.TitleUpdateType;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageCreatedEvent;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TitleEventsHandler {
    private final TitleHistoryService titleHistoryService;

    @EventListener
    public void handleCreatedEvent(WikiPageCreatedEvent event) {
        titleHistoryService.append(
                event.wikiPageTitle(),
                TitleUpdateType.CREATED,
                event.timestamp()
        );
    }

    @EventListener
    public void handleDeletedEvent(WikiPageDeletedEvent event) {
        titleHistoryService.append(
                event.deletedTitle(),
                TitleUpdateType.DELETED,
                event.timestamp()
        );
    }
}
