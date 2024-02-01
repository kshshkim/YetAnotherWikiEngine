package dev.prvt.yawiki.member.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherImplTest {
    PasswordHasherImpl passwordHasher = new PasswordHasherImpl();

    String givenPassword;

    @BeforeEach
    void init() {
        givenPassword = randString() + randString();
    }

    @Test
    void hash_matches_success() {
        // given
        String hashed = passwordHasher.hash(givenPassword);

        // when
        assertThat(passwordHasher.matches(givenPassword, hashed))
            .describedAs("일치하는 비밀번호를 입력하였을 때, 검증에 성공해야함.")
            .isTrue();
    }

    @Test
    void hash_matches_fail() {
        // given
        String hashed = passwordHasher.hash(givenPassword + "hi");

        // when
        assertThat(passwordHasher.matches(givenPassword, hashed))
            .describedAs("값이 다르기 때문에 검증에 실패해야함.")
            .isFalse();
    }
}