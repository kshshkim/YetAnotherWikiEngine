package dev.prvt.yawiki.app.wikipage;

import dev.prvt.yawiki.app.wikipage.domain.RawContent;
import dev.prvt.yawiki.app.wikipage.domain.Revision;
import dev.prvt.yawiki.app.wikipage.domain.WikiPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class WikiPageTest {

    WikiPage givenDoc;
    String givenComment;
    String givenContent;
    UUID givenContributorId;

    @BeforeEach
    void beforeEach() {
        givenDoc = WikiPage.create(randString());

        givenComment = "comment " + randString();
        givenContent = "content " + randString();
        givenContributorId = UUID.randomUUID();
    }

    @Test
    void creationTest() {
        // given
        WikiPage wikiPage = WikiPage.create(randString());
        String comment = "comment " + randString();
        String content = "content" + randString();
        // when
        wikiPage.update(givenContributorId, comment, content);

        // then
        Revision rev = wikiPage.getCurrentRevision();

        assertThat(rev).isNotNull();
        assertThat(rev.getWikiPage())
                .isNotNull()
                .isSameAs(wikiPage);
        assertThat(rev.getRevVersion()).isEqualTo(1);
        assertThat(rev.getComment()).isEqualTo(comment);

        RawContent raw = rev.getRawContent();
        assertThat(raw.getContent()).isEqualTo(content);

    }

    @Test
    void should_be_created_with_edit_token() {
        String editToken = givenDoc.getEditToken();

        assertThat(editToken)
                .isNotNull()
                .describedAs("현재 UUID를 토큰으로 사용중임.")
                .hasSameSizeAs(UUID.randomUUID().toString());
    }

    @Test
    void should_success_update_when_current_revision_is_null() {
        // given

        // when
        givenDoc.update(givenContributorId, givenComment, givenContent);

        // then
        Revision rev = givenDoc.getCurrentRevision();
        assertThat(rev)
                .isNotNull();
        assertThat(rev.getWikiPage())
                .isNotNull()
                .isSameAs(givenDoc);
        assertThat(rev.getRevVersion())
                .isEqualTo(1);

        RawContent raw = rev.getRawContent();

        assertThat(raw)
                .isNotNull();
        assertThat(raw.getContent())
                .isEqualTo(givenContent);
    }

    @Test
    void should_regenerate_edit_token_when_successfully_updated() {
        // given
        String givenEditToken = givenDoc.getEditToken();
        assertThat(givenEditToken).isNotNull();

        // when
        givenDoc.update(givenContributorId, givenComment, givenContent);

        // then
        assertThat(givenDoc.getEditToken()).isNotEqualTo(givenEditToken);
    }

    @Test
    void should_rev_version_incremented_when_updated() {
        // given
        givenDoc.update(givenContributorId, givenComment, givenContent);
        Revision givenRev = givenDoc.getCurrentRevision();
        RawContent givenRaw = givenRev.getRawContent();
        String newComment = "comment " + randString();
        String newContent = "content " + randString() + randString();

        // when
        givenDoc.update(givenContributorId, newComment, newContent);

        // then
        Revision newRev = givenDoc.getCurrentRevision();
        assertThat(newRev)
                .isNotNull()
                .isNotSameAs(givenRev);

        assertThat(newRev.getRevVersion())
                .isGreaterThan(givenRev.getRevVersion());
        assertThat(newRev.getWikiPage())
                .isNotNull()
                .isSameAs(givenDoc);
        assertThat(newRev.getComment())
                .isEqualTo(newComment);

        RawContent newRaw = newRev.getRawContent();
        assertThat(newRaw)
                .isNotNull()
                .isNotSameAs(givenRaw);
        assertThat(newRaw.getContent())
                .isEqualTo(newContent)
                .isNotEqualTo(givenRaw.getContent());

    }

}