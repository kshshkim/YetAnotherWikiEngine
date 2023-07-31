package dev.prvt.yawiki.application.domain.wikipage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentTest {

    Document givenDoc;
    Revision givenRev;
    DocumentEditValidator validator = new DummyValidator();

    @BeforeEach
    void beforeEach() {
        givenDoc = Document.create(randString());
        givenRev = new Revision(givenDoc, randString());
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
        givenDoc.updateDocument(givenRev);

        // then
        assertThat(givenDoc.getCurrentRevision()).isSameAs(givenRev);
    }

    @Test
    void should_rev_version_incremented_when_updated() {
        // given
        givenDoc.updateDocument(givenRev);
        long givenVersion = givenDoc.getCurrentRevision().getRevVersion();
        Revision givenRev = new Revision(givenDoc, randString());

        // when
        givenDoc.updateDocument(givenRev);

        // then
        assertThat(givenDoc.getCurrentRevision().getRevVersion())
                .isEqualTo(givenVersion+1L);
    }

}