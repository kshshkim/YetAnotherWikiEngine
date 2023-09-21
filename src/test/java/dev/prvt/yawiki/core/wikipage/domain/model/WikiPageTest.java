package dev.prvt.yawiki.core.wikipage.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static dev.prvt.yawiki.Fixture.updateWikiPageRandomly;
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
        String editToken = givenDoc.getVersionToken();

        assertThat(editToken)
                .isNotNull()
                .describedAs("현재 UUID를 토큰으로 사용중임.")
                .hasSameSizeAs(UUID.randomUUID().toString());
    }

    @Test
    void should_be_created_with_default_owner_group() {
        UUID givenOwnerGroupId = givenDoc.getOwnerGroupId();

        assertThat(givenOwnerGroupId)
                .describedAs("owner group id parameter 없이 생성될 경우, 기본값으로 \"00000000-0000-0000-0000-000000000001\"이 들어가야함.")
                .isNotNull()
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    void should_be_created_with_owner_group() {
        // given
        UUID givenUuid = UUID.randomUUID();
        String givenTitle = randString();

        // when
        WikiPage created = WikiPage.create(givenTitle, givenUuid);

        // then
        assertThat(created.getOwnerGroupId()).isEqualTo(givenUuid);
        assertThat(created.getTitle()).isEqualTo(givenTitle);
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
        String givenEditToken = givenDoc.getVersionToken();
        assertThat(givenEditToken).isNotNull();

        // when
        givenDoc.update(givenContributorId, givenComment, givenContent);

        // then
        assertThat(givenDoc.getVersionToken()).isNotEqualTo(givenEditToken);
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

    @Test
    void isActive_should_be_set_false_when_created() {
        assertThat(givenDoc.isActive())
                .isFalse();
    }

    @Test
    void isActive_should_be_true_after_update() {
        // when
        givenDoc.update(givenContributorId, givenComment, givenContent);

        // then
        assertThat(givenDoc.isActive())
                .isTrue();
    }

    @Test
    void delete_test() {
        // given
        givenDoc.update(givenContributorId, givenComment, givenContent);
        givenComment = randString();
        Revision givenRevision = givenDoc.getCurrentRevision();

        // when
        givenDoc.delete(givenContributorId, givenComment);

        // then
        assertThat(givenDoc.isActive())
                .describedAs("isActive status should be changed")
                .isFalse();

        assertThat(givenDoc.getContent())
                .describedAs("content should be blank")
                .isBlank();

        Revision currentRevision = givenDoc.getCurrentRevision();

        assertThat(currentRevision)
                .isNotNull();

        assertThat(currentRevision.getRevVersion())
                .isNotEqualTo(givenRevision.getRevVersion());


    }

    @Test
    void getContent_should_return_blank_string_if_current_rev_is_null() {
        // given
        WikiPage givenWikiPage = WikiPage.create(randString());
        assertThat(givenWikiPage.getCurrentRevision())
                .describedAs("테스트 선행 조건 만족")
                .isNull();

        // when
        String content = givenWikiPage.getContent();

        // then
        assertThat(content).isEqualTo("");
    }

    @Test
    void getContent_should_return_correct_string() {
        // given
        WikiPage wikiPage = WikiPage.create(randString());
        updateWikiPageRandomly(wikiPage);
        updateWikiPageRandomly(wikiPage);

        // when
        String content = wikiPage.getContent();

        // then
        assertThat(content)
                .isEqualTo(wikiPage
                        .getCurrentRevision()
                        .getRawContent()
                        .getContent());
    }

}