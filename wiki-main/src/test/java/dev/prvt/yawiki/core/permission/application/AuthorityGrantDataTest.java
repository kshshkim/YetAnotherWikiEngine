package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorityGrantDataTest {

    @Test
    void creationTest() {
        assertThatCode(() -> new AuthorityGrantData(
                UUID.randomUUID(),
                UUID.randomUUID(),
                PermissionLevel.MEMBER,
                LocalDateTime.now(),
                randString()
        ))
                .doesNotThrowAnyException();

    }

    @Test
    void constructionTest_not_null() {
        assertThatThrownBy(() -> new AuthorityGrantData(
                null,
                UUID.randomUUID(),
                PermissionLevel.MEMBER,
                LocalDateTime.now(),
                randString()
        ))
                .isInstanceOf(NullPointerException.class);

    }
}