package dev.prvt.yawiki.core.wikipage.infra.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.VersionCollisionException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionCollisionValidatorImplTest {
    VersionCollisionValidatorImpl versionCollisionValidator = new VersionCollisionValidatorImpl();

    @Test
    void validate_should_fail() {
        WikiPage givenWikiPage = WikiPage.create(randString());
        assertThatThrownBy(() -> versionCollisionValidator.validate(givenWikiPage, UUID.randomUUID().toString()))
                .describedAs("토큰 불일치시 실패하여야하고, 지정된 예외를 반환해야함.")
                .isInstanceOf(VersionCollisionException.class);
    }

    @Test
    void validate_should_success() {
        WikiPage givenWikiPage = WikiPage.create(randString());
        assertThatCode(() -> versionCollisionValidator.validate(givenWikiPage, givenWikiPage.getVersionToken()))
                .describedAs("동일한 토큰이 들어왔으니 성공해야함.")
                .doesNotThrowAnyException();
    }
}