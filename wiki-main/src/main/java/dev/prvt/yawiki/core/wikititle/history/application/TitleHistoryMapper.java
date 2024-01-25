package dev.prvt.yawiki.core.wikititle.history.application;

import dev.prvt.yawiki.common.model.TitleUpdateType;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageCreatedEvent;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleHistory;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleHistoryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TitleHistoryMapper {
    private final TitleHistoryFactory titleHistoryFactory;

    public TitleHistory mapFrom(WikiPageCreatedEvent wikiPageCreatedEvent) {
        return titleHistoryFactory.create(
                wikiPageCreatedEvent.wikiPageTitle(),
                TitleUpdateType.CREATED,
                LocalDateTime.now()
        );
    }
}
