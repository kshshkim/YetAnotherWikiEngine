package dev.prvt.yawiki.core.wikipage.domain.model;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static dev.prvt.yawiki.fixture.WikiPageFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class WikiPageTest {

    WikiPage givenWikiPage;
    String givenComment;
    String givenContent;
    UUID givenContributorId;

    @BeforeEach
    void beforeEach() {
        givenWikiPage = aNormalWikiPage();

        givenComment = "comment " + randString();
        givenContent = "content " + randString();
        givenContributorId = UUID.randomUUID();
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
        WikiPage givenWikiPage = aNormalWikiPage();
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
        WikiPage wikiPage = aNormalWikiPage();
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

    @Test
    void getLastModifiedAt_when_modified() {
        // given
        WikiPage wikiPage = aNormalWikiPage();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdTime = wikiPage.getLastModifiedAt();

        // when
        updateWikiPageRandomly(wikiPage);

        // then
        LocalDateTime lastModifiedAt = wikiPage.getLastModifiedAt();

        assertThat(lastModifiedAt)
                .describedAs("수정된 순간 필드도 업데이트됨.")
                .isAfter(createdTime);

        assertThat(lastModifiedAt)
                .describedAs("수정된 시간이 적절히 설정됨.")
                .isBetween(now.minusSeconds(1), now.plusSeconds(1));
    }

    @Test
    void getLastModifiedAt_when_created() {
        // given
        WikiPage wikiPage = aNormalWikiPage();
        LocalDateTime now = LocalDateTime.now();

        // when

        // then
        LocalDateTime lastModifiedAt = wikiPage.getLastModifiedAt();
        assertThat(lastModifiedAt)
                .describedAs("생성된 시간이 적절히 설정됨.")
                .isBetween(now.minusSeconds(1), now.plusSeconds(1));
    }


    @Test
    void activated_true_after_modified_not_active() {
        // given
        WikiPage wikiPage = aNormalWikiPage();
        LocalDateTime now = LocalDateTime.now();

        // when
        updateWikiPageRandomly(wikiPage);

        // then
        assertThat(wikiPage.isActivated())
                .describedAs("문서가 생성되었으므로 true")
                .isTrue();
    }

    /**
     * 이미 active 값이 참인 상태에서 수정하는 경우, activated 는 false.
     */
    @SneakyThrows
    @Test
    void activated_false_when_already_active() {
        // given
        WikiPage wikiPage = aNormalWikiPage();
        LocalDateTime now = LocalDateTime.now();
        updateWikiPageRandomly(wikiPage);

        setWikiPageActivated(wikiPage, false);

        // when
        updateWikiPageRandomly(wikiPage);

        // then
        assertThat(wikiPage.isActivated())
                .describedAs("이미 생성된 상태였으므로 false")
                .isFalse();
    }
}