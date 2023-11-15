package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class WikiPageMapper {
    public WikiPageDataForUpdate mapFrom(WikiPage wikiPage) {
        return new WikiPageDataForUpdate(wikiPage.getTitle(), wikiPage.getNamespace(), wikiPage.getContent(), wikiPage.getVersionToken());
    }

    private String extractContributorName(Contributor contributor) {
        if (contributor == null) {
            return "NULL";
        }
        return contributor.getName();
    }

    public RevisionData mapFrom(Revision revision, Map<UUID, Contributor> contributorMap) {
        String contributorName = extractContributorName(contributorMap.get(revision.getContributorId()));

        return new RevisionData(
                revision.getRevVersion(),
                revision.getDiff(),
                contributorName,
                revision.getComment()
        );
    }
}
