package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.springframework.stereotype.Component;

@Component
public class WikiPageMapper {
    public WikiPageDataForUpdate mapForUpdate(WikiPage wikiPage) {
        return new WikiPageDataForUpdate(wikiPage.getTitle(), wikiPage.getNamespace(), wikiPage.getContent(), wikiPage.getVersionToken());
    }

    public WikiPageDataForUpdate mapForUpdate(Revision revision) {
        WikiPage wikiPage = revision.getWikiPage();
        return new WikiPageDataForUpdate(wikiPage.getTitle(), wikiPage.getNamespace(), revision.getContent(), wikiPage.getVersionToken());
    }

    public WikiPageDataForRead mapForRead(WikiPage wikiPage) {
        return new WikiPageDataForRead(wikiPage.getWikiPageTitle(), wikiPage.getContent());
    }

    public WikiPageDataForRead mapForRead(Revision revision) {
        return new WikiPageDataForRead(revision.getWikiPage().getWikiPageTitle(), revision.getContent());
    }

    public RevisionData mapFrom(Revision revision) {
        return new RevisionData(
                revision.getRevVersion(),
                revision.getDiff(),
                revision.getContributorId(),
                revision.getComment()
        );
    }
}
