package dev.prvt.yawiki.member.application;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.prvt.yawiki.member.domain.Member;
import dev.prvt.yawiki.member.domain.MemberRepository;
import dev.prvt.yawiki.member.domain.PasswordHasher;
import dev.prvt.yawiki.member.exception.MemberNotFoundException;
import dev.prvt.yawiki.member.exception.PasswordMismatchException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 회원 서비스 통합 테스트
 */
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    MemberService memberService;

    @Test
    void join() {
        // given
        String givenUsername = randString();
        String givenPassword = randString();

        // when
        MemberData join = memberService.join(new MemberJoinData(givenUsername, givenPassword));

        // then
        Optional<Member> found = memberRepository.findByUsername(givenUsername);

        assertThat(found)
            .describedAs("가입에 성공하여 존재해야함.")
            .isPresent();

        Member member = found.orElseThrow();

        assertThat(member.getUsername())
            .describedAs("찾아온 Member 유저네임이 일치해야함.")
            .isEqualTo(givenUsername);

        assertThat(passwordHasher.matches(givenPassword, member.getPassword()))
            .describedAs("패스워드 해싱이 정상적으로 적용되어, 일치해야함.")
            .isTrue();
    }

    @Test
    void verifyPassword() {
        // given
        String givenUsername = randString();
        String givenPassword = randString();

        memberService.join(new MemberJoinData(givenUsername, givenPassword));

        // when then
        assertThatCode(() -> memberService.verifyPassword(new MemberPasswordVerificationData(givenUsername, givenPassword)))
            .describedAs("성공하여 예외가 발생하지 않아야함.")
            .doesNotThrowAnyException();

        String wrongUsername = "not" + givenUsername;

        assertThatThrownBy(() -> memberService.verifyPassword(new MemberPasswordVerificationData(wrongUsername, givenPassword)))
            .describedAs("없는 회원이기 때문에 예외를 반환해야함.")
            .isInstanceOf(MemberNotFoundException.class);

        String wrongPassword = "not" + givenPassword;

        assertThatThrownBy(() -> memberService.verifyPassword(new MemberPasswordVerificationData(givenUsername, wrongPassword)))
            .describedAs("비밀번호가 일치하지 않기 때문에 예외를 반환해야함.")
            .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    void updatePassword() {
        // given
        String givenUsername = randString();
        String givenPassword = randString();
        String newPassword = randString();

        memberService.join(new MemberJoinData(givenUsername, givenPassword));


        // when then
        assertThatCode(() -> memberService.updatePassword(new MemberPasswordUpdateData(givenUsername, givenPassword, newPassword)))
            .describedAs("비밀번호가 일치하기 때문에 예외가 발생하지 않아야함.")
            .doesNotThrowAnyException();

        assertThatCode(() -> memberService.verifyPassword(new MemberPasswordVerificationData(givenUsername, newPassword)))
            .describedAs("변경된 비밀번호로 인증이 가능해야함.")
            .doesNotThrowAnyException();

        assertThatThrownBy(() -> memberService.verifyPassword(new MemberPasswordVerificationData(givenUsername, givenPassword)))
            .describedAs("변경 이전 비밀번호를 사용하여 인증하면 예외가 발생해야함.")
            .isInstanceOf(PasswordMismatchException.class);
    }

}