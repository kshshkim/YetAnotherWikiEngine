package dev.prvt.yawiki.core.wikipage.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static dev.prvt.yawiki.fixture.WikiPageFixture.updateWikiPageRandomly;
import static org.assertj.core.api.Assertions.assertThat;

class WikiPageTest {

    WikiPage givenWikiPage;
    String givenComment;
    String givenContent;
    UUID givenContributorId;

    @BeforeEach
    void beforeEach() {
        givenWikiPage = WikiPage.create(randString(), Namespace.NORMAL);

        givenComment = "comment " + randString();
        givenContent = "content " + randString();
        givenContributorId = UUID.randomUUID();
    }

    @Test
    void create_only_with_title() {
        // given
        String givenTitle = randString();

        // when
        WikiPage wikiPage = WikiPage.create(givenTitle);

        // then
        assertThat(wikiPage.isActive())
                .describedAs("만들어진 직후에는 isActive가 false여야함.")
                .isFalse();

        assertThat(wikiPage.getTitle())
                .describedAs("제목이 제대로 설정되어야함.")
                .isEqualTo(givenTitle);

        assertThat(wikiPage.getNamespace())
                .describedAs("namespace 기본값은 NORMAL임.")
                .isEqualTo(Namespace.NORMAL);

        assertThat(wikiPage.getVersionToken())
                .describedAs("버전 토큰이 초기화 되어야함. 공백, 혹은 null이어서는 안 됨.")
                .isNotNull()
                .isNotBlank();
    }

    @Test
    void create_with_title_and_namespace() {
        // given
        String givenTitle = randString();
        Namespace givenNamespace = Namespace.MAIN;

        // when
        WikiPage wikiPage = WikiPage.create(givenTitle, givenNamespace);

        // then
        assertThat(wikiPage.isActive())
                .describedAs("만들어진 직후에는 isActive가 false여야함.")
                .isFalse();

        assertThat(wikiPage.getTitle())
                .describedAs("제목이 제대로 설정되어야함.")
                .isEqualTo(givenTitle);

        assertThat(wikiPage.getNamespace())
                .describedAs("namespace가 제대로 설정되어야함.")
                .isEqualTo(givenNamespace);

        assertThat(wikiPage.getVersionToken())
                .describedAs("버전 토큰이 초기화 되어야함. 공백, 혹은 null이어서는 안 됨.")
                .isNotNull()
                .isNotBlank();
    }

    @Test
    void create_isActive_should_be_set_false_when_created() {
        assertThat(givenWikiPage.isActive())
                .isFalse();
    }

    @Test
    void update_should_success_when_current_revision_is_null() {
        // given

        // when
        givenWikiPage.update(givenContributorId, givenComment, givenContent);

        // then
        Revision rev = givenWikiPage.getCurrentRevision();
        assertThat(rev)
                .describedAs("새로 생성된 리비전이 설정돼야함.")
                .isNotNull();

        assertThat(rev.getWikiPage())
                .describedAs("Revision.wikiPage가 givenWikiPage로 설정돼야함.")
                .isNotNull()
                .isSameAs(givenWikiPage);

        assertThat(rev.getRevVersion())
                .describedAs("처음 생성된 리비전의 버전은 1임.")
                .isEqualTo(1);

        RawContent raw = rev.getRawContent();

        assertThat(raw)
                .describedAs("RawContent가 생성되어야함.")
                .isNotNull();

        assertThat(raw.getContent())
                .describedAs("RawContent의 본문이 제대로 생성되어야함.")
                .isEqualTo(givenContent);
    }

    @Test
    void update_should_regenerate_edit_token_when_successfully_updated() {
        // given
        String givenEditToken = givenWikiPage.getVersionToken();
        assertThat(givenEditToken).isNotNull();

        // when
        givenWikiPage.update(givenContributorId, givenComment, givenContent);

        // then
        assertThat(givenWikiPage.getVersionToken())
                .describedAs("버전 토큰이 재생성되어 이전의 버전 토큰과 일치하지 않아야함.")
                .isNotEqualTo(givenEditToken)
                .isNotNull()
                .isNotBlank();
    }

    @Test
    void update_should_rev_version_incremented_when_updated() {
        // given
        givenWikiPage.update(givenContributorId, givenComment, givenContent);
        Revision givenRev = givenWikiPage.getCurrentRevision();
        RawContent givenRaw = givenRev.getRawContent();
        String newComment = "comment " + randString();
        String newContent = "content " + randString() + randString();

        // when
        givenWikiPage.update(givenContributorId, newComment, newContent);

        // then
        Revision newRev = givenWikiPage.getCurrentRevision();
        assertThat(newRev)
                .describedAs("새 Revision이 생성돼야함.")
                .isNotNull()
                .isNotSameAs(givenRev);

        assertThat(newRev.getRevVersion())
                .describedAs("이전 revVersion보다 커야함.")
                .isGreaterThan(givenRev.getRevVersion());

        assertThat(newRev.getWikiPage())
                .describedAs("새로 생성된 Revision의 wikiPage가 제대로 설정됨.")
                .isNotNull()
                .isSameAs(givenWikiPage);

        assertThat(newRev.getComment())
                .describedAs("comment가 제대로 설정됨.")
                .isEqualTo(newComment);

        RawContent newRaw = newRev.getRawContent();

        assertThat(newRaw)
                .describedAs("새 RawContent가 설정돼야함.")
                .isNotNull()
                .isNotSameAs(givenRaw);

        assertThat(newRaw.getContent())
                .describedAs("새 RawContent의 본문이 제대로 설정되어야함.")
                .isEqualTo(newContent)
                .describedAs("이전의 본문과 달라야함.")
                .isNotEqualTo(givenRaw.getContent());

    }

    @Test
    void update_isActive_should_be_true_after_update() {
        // when
        givenWikiPage.update(givenContributorId, givenComment, givenContent);

        // then
        assertThat(givenWikiPage.isActive())
                .isTrue();
    }

    @Test
    void delete_test() {
        // given
        givenWikiPage.update(givenContributorId, givenComment, givenContent);
        givenComment = randString();
        Revision givenRevision = givenWikiPage.getCurrentRevision();

        // when
        givenWikiPage.delete(givenContributorId, givenComment);

        // then
        assertThat(givenWikiPage.isActive())
                .describedAs("isActive status should be changed")
                .isFalse();

        assertThat(givenWikiPage.getContent())
                .describedAs("content should be blank")
                .isBlank();

        Revision currentRevision = givenWikiPage.getCurrentRevision();

        assertThat(currentRevision)
                .describedAs("현재 Revision이 null이어선 안 됨.")
                .isNotNull();

        assertThat(currentRevision.getRevVersion())
                .describedAs("이전 Revision과 버전 숫자가 달라야함.")
                .isNotEqualTo(givenRevision.getRevVersion())
                .describedAs("이전 Revision보다 버전 숫자가 커야함.")
                .isGreaterThan(givenRevision.getRevVersion())
        ;
    }

    @Test
    void getContent_should_return_blank_string_if_current_rev_is_null() {
        // given
        WikiPage givenWikiPage = WikiPage.create(randString(), Namespace.NORMAL);
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
        WikiPage wikiPage = WikiPage.create(randString(), Namespace.NORMAL);
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