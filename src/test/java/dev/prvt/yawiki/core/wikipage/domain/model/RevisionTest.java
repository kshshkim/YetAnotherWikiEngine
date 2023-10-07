package dev.prvt.yawiki.core.wikipage.domain.model;

import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

class RevisionTest {
    Revision testRev;
    WikiPage testWikiPage;
    UUID testContributorId;
    @BeforeEach
    void beforeEach() {
        testWikiPage = WikiPage.create(randString());
        testContributorId = UUID.randomUUID();
        testRev = WikiPageFixture.aRevision()
                .contributorId(testContributorId)
                .wikiPage(testWikiPage)
                .build();
    }
    @Test
    void construction_contributorId_should_be_set() {
        assertThat(testRev.getContributorId())
                .isNotNull()
                .isEqualTo(testContributorId)
        ;
    }

    @Test
    void construction_should_throw_exception_if_contributorId_is_null() {
        assertThatThrownBy(() -> Revision.builder().contributorId(null).build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("contributorId");
    }


    @Test
    void construction_asAfter_can_be_called_only_in_constructor() throws NoSuchMethodException {
        Class<? extends Revision> revClass = testRev.getClass();
        Method asAfter = revClass.getDeclaredMethod("asAfter", Revision.class);
        asAfter.setAccessible(true);

        Revision ex = WikiPageFixture.aRevision()
                .build();

        // then
        assertThatThrownBy(() -> asAfter.invoke(testRev, ex))
                .describedAs("should fail when already called")
                .getCause()
                .hasMessageContaining("finalized");
    }

    @Test
    void construction_asAfter_version_should_be_incremented_when_update() {
        assertThat(testRev.getRevVersion()).isEqualTo(1L);

        // when
        Revision newerRev = WikiPageFixture.aRevision()
                .contributorId(testContributorId)
                .wikiPage(testWikiPage)
                .beforeRevision(testRev)
                .build();

        // then
        assertThat(newerRev.getRevVersion()).isEqualTo(2L);
    }

    @Test
    void construction_asAfter_diff_should_be_the_size_of_itself_when_beforeRev_is_null() {
        // given
        Revision given = Revision.builder()
                .rawContent(new RawContent("sizeOf7"))
                .beforeRevision(null)
                .build();

        // when
//        given.asAfter(null);

        // then
        assertThat(given.getDiff())
                .isEqualTo(7);
    }

    @Test
    void construction_asAfter_diff_should_be_calculated_correctly() {
        Revision revSizeOf9A = Revision.builder()
                .rawContent(new RawContent("size.Of.9"))
                .beforeRevision(null)
                .build();
        Revision revSizeOf7 = Revision.builder()
                .rawContent(new RawContent("sizeOf7"))
                .beforeRevision(revSizeOf9A)
                .build();
        Revision revSizeOf9B = Revision.builder()
                .rawContent(new RawContent("size.Of.9"))
                .beforeRevision(revSizeOf7)
                .build();

        // then
        assertThat(revSizeOf7.getDiff())
                .isEqualTo(-2);
        assertThat(revSizeOf9B.getDiff())
                .isEqualTo(2);
    }

    @Test
    void getSize_should_calculate_correctly() {
        // given
        Revision given = Revision.builder()
                .rawContent(new RawContent("12345"))
                .build();
        // when
        int size = given.getSize();

        // then
        assertThat(size).isEqualTo(5);
    }

    @Test
    void getDiff_should_return_size_of_itself_when_it_does_not_have_before_rev() {
        // given
        Revision revisionThatHasContentSizeOf9 = Revision.builder()
                .rawContent(new RawContent("123456789"))
                .contributorId(UUID.randomUUID())
                .comment("")
                .beforeRevision(null)
                .build();

        // then
        assertThat(revisionThatHasContentSizeOf9.getDiff()).isEqualTo(9);
    }

    @Test
    void getContent_return_correct_string() {
        RawContent givenRaw = WikiPageFixture.aRawContent();
        Revision givenRev = WikiPageFixture.aRevision()
                .rawContent(givenRaw).build();

        // when
        String content = givenRev.getContent();

        // then
        assertThat(content)
                .isEqualTo(givenRaw.getContent());
    }

    @Test
    void getContent_must_not_return_null() {
        Revision givenRev = WikiPageFixture.aRevision()
                .rawContent(null)
                .build();

        // when
        String content = givenRev.getContent();

        // then
        assertThat(content)
                .isNotNull()
                .isEqualTo("");
    }

    @Test
    void getContent_when_givenRev_is_not_null_but_size_is_0() {
        Revision givenRev = WikiPageFixture.aRevision()
                .rawContent(new RawContent(""))
                .build();

        // when
        String content = givenRev.getContent();

        // then
        assertThat(content)
                .isNotNull()
                .isEqualTo("");
    }
}