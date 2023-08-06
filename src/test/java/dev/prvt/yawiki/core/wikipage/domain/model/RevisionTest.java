package dev.prvt.yawiki.core.wikipage.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.*;
import static org.assertj.core.api.Assertions.*;

class RevisionTest {
    Revision testRev;
    WikiPage testWikiPage;
    UUID testContributorId;
    @BeforeEach
    void beforeEach() {
        testWikiPage = WikiPage.create(randString());
        testContributorId = UUID.randomUUID();
        testRev = aRevision()
                .contributorId(testContributorId)
                .wikiPage(testWikiPage)
                .build();
    }


    @Test
    void versionCannotBeChanged() {
        // when
        assertThatCode(() -> testRev.setRevVersionAfter(null))
                .describedAs("should success when version is null")
                .doesNotThrowAnyException();

        // then
        assertThatThrownBy(() -> testRev.setRevVersionAfter(null))
                .describedAs("should fail when version has been set")
                .hasMessageContaining("finalized");
    }

    @Test
    void rawContentCannotBeChanged() {
        // given
        testRev = aRevision()
                .rawContent(null)
                .wikiPage(testWikiPage)
                .build();

        // when
        assertThatCode(() -> testRev.setRawContent(new RawContent(randString())))
                .describedAs("should success when rawContent is null")
                .doesNotThrowAnyException();

        // then
        assertThatThrownBy(() -> testRev.setRawContent(new RawContent(randString())))
                .describedAs("should fail when rawContent has been set")
                .hasMessageContaining("finalized");
    }

    @Test
    void version_should_be_incremented_when_update() {
        testRev.setRevVersionAfter(null);
        assertThat(testRev.getRevVersion()).isEqualTo(1L);

        // when
        Revision newerRev = aRevision()
                .contributorId(testContributorId)
                .wikiPage(testWikiPage)
                .build();

        newerRev.setRevVersionAfter(testRev);

        // then
        assertThat(newerRev.getRevVersion()).isEqualTo(2L);
    }

    @Test
    void contributorId_should_be_set() {
        assertThat(testRev.getContributorId())
                .isNotNull()
                .isEqualTo(testContributorId)
        ;
    }

    @Test
    void should_throw_exception_if_contributorId_is_null() {
        Revision.builder()
                .contributorId(null)
                .build();
    }
}