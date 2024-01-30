package dev.prvt.yawiki.member.application;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.member.domain.Member;
import dev.prvt.yawiki.member.domain.MemberRepository;
import dev.prvt.yawiki.member.domain.PasswordHasher;
import dev.prvt.yawiki.member.dto.MemberJoinDto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberServiceImplTest {
//    private MemberRepository memberRepository = new MemberMemoryRepository();
//    private PasswordHasher passwordHasher = new PasswordHasherImpl();
//    private UuidGenerator uuidGenerator = UuidGenerators.UUID_V7_INSTANCE;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    UuidGenerator uuidGenerator;

    private MemberServiceImpl memberService;

    @BeforeEach
    void init() {

        memberService = new MemberServiceImpl(memberRepository, passwordHasher, uuidGenerator);
    }

    @Test
    @Transactional
    void join() {
        // given
        String givenUsername = randString();
        String givenPassword = randString();

        // when
        Member join = memberService.join(new MemberJoinDto(givenUsername, givenPassword));

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

}