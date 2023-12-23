package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class WikiPageEventFactory {

    public WikiPageActivatedEvent wikiPageActivatedEvent(WikiPage wikiPage) {
        return new WikiPageActivatedEvent(wikiPage.getWikiPageTitle(), wikiPage.getLastModifiedAt());
    }

    public WikiPageCreatedEvent wikiPageCreatedEvent(WikiPage wikiPage) {
        return new WikiPageCreatedEvent(wikiPage.getId(), wikiPage.getWikiPageTitle(), wikiPage.getLastModifiedAt());
    }

    public WikiPageDeletedEvent wikiPageDeletedEvent(WikiPage wikiPage) {
        return new WikiPageDeletedEvent(wikiPage.getLastModifiedBy(), wikiPage.getId(), wikiPage.getWikiPageTitle(), wikiPage.getLastModifiedAt());
    }

    public WikiPageUpdateCommittedEvent wikiPageUpdateCommittedEvent(WikiPage wikiPage, Set<WikiPageTitle> referencedTitles) {
        return new WikiPageUpdateCommittedEvent(wikiPage.getLastModifiedBy(), wikiPage.getId(), wikiPage.getWikiPageTitle(), referencedTitles);
    }

}
