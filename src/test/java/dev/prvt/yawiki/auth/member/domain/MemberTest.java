package dev.prvt.yawiki.auth.member.domain;

import dev.prvt.yawiki.auth.member.exception.PasswordMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.*;

class MemberTest {

    private UUID givenId;
    private String givenUsername;
    private Member givenMember;
    private String givenPassword;


    PasswordHasher mockPasswordHasher = new PasswordHasher() {
        @Override
        public String hash(String toHash) {
            return String.valueOf(toHash.hashCode());
        }

        @Override
        public boolean matches(String raw, String hashed) {
            return String.valueOf(raw.hashCode()).equals(hashed);
        }
    };

    @BeforeEach
    void init() {
        givenId = UUID.randomUUID();
        givenUsername = randString();
        givenPassword = randString();
        givenMember = Member.create(givenId, givenUsername, givenPassword, mockPasswordHasher);
    }

    @Test
    void updatePassword() {
        // when
        String givenHashedPassword = givenMember.getPassword();
        givenMember.updatePassword(randString(), mockPasswordHasher);
        // then
        assertThat(givenMember.getPassword())
                .isNotEqualTo(givenHashedPassword);
    }

    @Test
    void validatePassword() {
        assertThatCode(() -> givenMember.validatePassword(givenPassword, mockPasswordHasher))
                .doesNotThrowAnyException();
    }

    @Test
    void validatePassword_should_throw_exception() {
        assertThatThrownBy(() -> givenMember.validatePassword(givenPassword + "noo", mockPasswordHasher))
                .isInstanceOf(PasswordMismatchException.class);
    }
}