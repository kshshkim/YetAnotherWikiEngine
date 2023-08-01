package dev.prvt.yawiki.application.domain.wikipage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dev.prvt.yawiki.Fixture.*;
import static org.assertj.core.api.Assertions.*;

class RevisionTest {
    Revision testRev;
    WikiPage testDoc;
    @BeforeEach
    void beforeEach() {
        testDoc = WikiPage.create(randString());
        testRev = new Revision(testDoc, randString());
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
        Revision newerRev = new Revision(testDoc, randString());
        newerRev.setRevVersionAfter(testRev);

        // then
        assertThat(newerRev.getRevVersion()).isEqualTo(2L);
    }
}