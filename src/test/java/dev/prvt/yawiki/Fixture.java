package dev.prvt.yawiki;

import dev.prvt.yawiki.app.wikipage.domain.RawContent;
import dev.prvt.yawiki.app.wikipage.domain.Revision;
import dev.prvt.yawiki.app.wikipage.domain.WikiPage;
import net.bytebuddy.utility.RandomString;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Fixture {
    public static String randString() {
        return RandomString.make();
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
