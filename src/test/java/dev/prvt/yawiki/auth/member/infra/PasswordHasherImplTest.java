package dev.prvt.yawiki.auth.member.infra;

import dev.prvt.yawiki.auth.member.infra.PasswordHasherImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherImplTest {
    PasswordHasherImpl passwordHasher = new PasswordHasherImpl(new BCryptPasswordEncoder());

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
        boolean matches = passwordHasher.matches(givenPassword, hashed);
        assertThat(matches).isTrue();
    }

    @Test
    void hash_matches_fail() {
        // given
        String hashed = passwordHasher.hash(givenPassword + "hi");

        // when
        boolean matches = passwordHasher.matches(givenPassword, hashed);
        assertThat(matches).isFalse();
    }
}