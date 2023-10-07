package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.MemberContributor;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class WikiPageMapperTest {
    private WikiPageMapper wikiPageMapper = new WikiPageMapper();

    @Test
    void mapFrom_WikiPage_new_WikiPage() {
        WikiPage wikiPage = WikiPage.create(randString());
        WikiPageDataForUpdate mapped = wikiPageMapper.mapFrom(wikiPage);

        assertThat(tuple(mapped.title(), mapped.content(), mapped.versionToken()))
                .isEqualTo(tuple(wikiPage.getTitle(), wikiPage.getContent(), wikiPage.getVersionToken()));

        assertThat(List.of(mapped.title(), mapped.content(), mapped.versionToken()))
                .doesNotContainNull();

        assertThat(List.of(mapped.title(), mapped.versionToken()))
                .doesNotContain("");
    }

    @Test
    void mapFrom_WikiPage_existing_WikiPage() {
        WikiPage wikiPage = WikiPage.create(randString());
        WikiPageFixture.updateWikiPageRandomly(wikiPage);
        WikiPageDataForUpdate mapped = wikiPageMapper.mapFrom(wikiPage);

        assertThat(tuple(mapped.title(), mapped.content(), mapped.versionToken()))
                .isEqualTo(tuple(wikiPage.getTitle(), wikiPage.getContent(), wikiPage.getVersionToken()));

        assertThat(List.of(mapped.title(), mapped.content(), mapped.versionToken()))
                .doesNotContainNull()
                .doesNotContain("");
    }

    @Test
    void mapFrom_Revision_Map_Contributor_null() {
        Revision givenRev = WikiPageFixture.aRevision().build();
        Map<UUID, Contributor> contributorMap = new HashMap<>();

        // when
        RevisionData revisionData = wikiPageMapper.mapFrom(givenRev, contributorMap);

        // then
        assertThat(revisionData.contributorName())
                .isEqualTo("NULL");
    }

    @Test
    void mapFrom_Revision_Map_Contributor_redacted() {
        Revision givenRev = WikiPageFixture.aRevision().build();
        MemberContributor givenContributor = WikiPageFixture.aMemberContributor()
                .id(givenRev.getContributorId())
                .build();

        Map<UUID, Contributor> contributorMap = new HashMap<>();
        contributorMap.put(givenRev.getContributorId(), givenContributor);

        // when
        RevisionData revisionData = wikiPageMapper.mapFrom(givenRev, contributorMap);

        // then
        assertThat(revisionData.contributorName())
                .isEqualTo(givenContributor.getName());
    }
}