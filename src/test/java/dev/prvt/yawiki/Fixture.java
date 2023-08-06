package dev.prvt.yawiki;

import dev.prvt.yawiki.core.wikipage.domain.model.RawContent;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import net.bytebuddy.utility.RandomString;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Fixture {
    public static String randString() {
        return RandomString.make();
    }

    public static RawContent aRawContent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append(UUID.randomUUID());
        }
        return new RawContent(sb.toString());
    }

    public static Revision.RevisionBuilder aRevision() {
        return Revision.builder()
                .rawContent(aRawContent())
                .comment(randString());
    }

    public static void updateWikiPageRandomly(WikiPage wikiPage) {
        wikiPage.update(UUID.randomUUID(), randString(), randString() + randString() + randString());
    }

    public static void updateWikiPageRandomlyWithContributorId(WikiPage wikiPage, UUID contributorId) {
        wikiPage.update(contributorId, randString(), randString() + randString() + randString());
    }

    public static void assertEqualRawContent(RawContent actual, RawContent expected) {
        assertNotNull(actual);
        assertNotNull(expected);

        assertThat(actual.getId()).isNotNull().isEqualTo(expected.getId());
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
    }

    public static void assertEqualRevision(Revision actual, Revision expected) {
        assertNotNull(actual);
        assertNotNull(expected);

        assertThat(actual.getComment()).isEqualTo(expected.getComment());
        assertThat(actual.getRevVersion()).isEqualTo(expected.getRevVersion());
        assertThat(actual.getWikiPage().getId()).isEqualTo(expected.getWikiPage().getId());
        assertThat(actual.getRawContent().getId()).isEqualTo(expected.getRawContent().getId());
    }

    public static void assertEqualWikiPage(WikiPage actual, WikiPage expected) {
        assertNotNull(actual);
        assertNotNull(expected);

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
    }
}
