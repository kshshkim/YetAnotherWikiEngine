package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;

import java.util.UUID;

public interface WikiPageEventPublisher {
    void created(WikiPage wikiPage);

    void activated(WikiPage wikiPage);

    void deactivated(WikiPage wikiPage);
}
