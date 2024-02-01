package dev.prvt.yawiki.member.domain;

import dev.prvt.yawiki.member.exception.PasswordMismatchException;
import dev.prvt.yawiki.member.infra.PasswordHasherImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.*;

class MemberTest {

    private UUID givenId;
    private String givenUsername;
    private Member givenMember;
    private String givenPassword;


    PasswordHasher passwordHasher = new PasswordHasher() {
        @Override
        public String hash(String toHash) {
            return String.valueOf(toHash.hashCode());
        }

        @Override
        public boolean matches(String raw, String hashed) {
            return String.valueOf(raw.hashCode()).equals(hashed);
        }
    };
//    PasswordHasher passwordHasher = new PasswordHasherImpl();

    @BeforeEach
    void init() {
        givenId = UUID.randomUUID();
        givenUsername = randString();
        givenPassword = randString();
        givenMember = Member.create(givenId, givenUsername, givenPassword, passwordHasher);
    }

    @Test
    void updatePassword() {
        // when
        String givenHashedPassword = givenMember.getPassword();
        String newPassword = randString();

        assertThatThrownBy(() -> givenMember.verifyPassword(newPassword, passwordHasher))
            .describedAs("(테스트 선제조건) 비밀번호가 일치하지 않기 때문에 예외가 발생해야함.")
            .isInstanceOf(PasswordMismatchException.class);

        givenMember.updatePassword(givenPassword, newPassword, passwordHasher);

        // then
        assertThat(givenMember.getPassword())
            .describedAs("올바른 패스워드를 입력했기 때문에 성공해야하며, 값이 업데이트 되어야함.")
            .isNotEqualTo(givenHashedPassword);

        assertThatCode(() -> givenMember.verifyPassword(newPassword, passwordHasher))
            .describedAs("정상적으로 변경되어 비밀번호 미일치 예외가 발생하지 않음.")
            .doesNotThrowAnyException();
    }

    @Test
    void updatePassword_wrong_old_password() {
        // when then
        assertThatThrownBy(() -> givenMember.updatePassword("not" + givenPassword, randString(), passwordHasher))
            .describedAs("이전 비밀번호가 일치하지 않으면 실패해야함.")
            .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    void validatePassword() {
        assertThatCode(() -> givenMember.verifyPassword(givenPassword, passwordHasher))
                .doesNotThrowAnyException();
    }

    @Test
    void validatePassword_should_throw_exception() {
        assertThatThrownBy(() -> givenMember.verifyPassword(givenPassword + "noo", passwordHasher))
                .isInstanceOf(PasswordMismatchException.class);
    }
}