package dev.prvt.yetanotherwikiengine.application.document.domain;

import dev.prvt.yetanotherwikiengine.Fixture;
import dev.prvt.yetanotherwikiengine.application.document.domain.dependency.DocumentEditValidator;
import dev.prvt.yetanotherwikiengine.application.document.domain.exception.EditValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dev.prvt.yetanotherwikiengine.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class RevisionTest {
    Revision testRev;
    Document testDoc;
    @BeforeEach
    void beforeEach() {
        testDoc = Document.create(randString());
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
        Revision newerRev = new Revision();
        newerRev.setRevVersionAfter(testRev);

        // then
        assertThat(newerRev.getRevVersion()).isEqualTo(2L);
    }
}