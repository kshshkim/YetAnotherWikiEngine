package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.Set;

public interface WikiPageEventPublisher {
    void created(WikiPage wikiPage);

    void activated(WikiPage wikiPage);

    void deactivated(WikiPage wikiPage);

    void updateCommitted(WikiPage wikiPage, Set<WikiPageTitle> referencedTitles);

    void renamed(WikiPage wikiPage, WikiPageTitle beforeTitle);
}
