package dev.prvt.yawiki.auth.member.application;

import com.fasterxml.uuid.Generators;
import dev.prvt.yawiki.auth.member.domain.*;
import dev.prvt.yawiki.common.uuid.FasterXmlNoArgGeneratorAdapter;
import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.auth.member.infra.PasswordHasherImpl;
import dev.prvt.yawiki.core.event.MemberJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.*;

class MemberServiceImplTest {
    private MemberRepository memberRepository = new MemberMemoryRepository();
    private PasswordHasher passwordHasher = new PasswordHasherImpl(new BCryptPasswordEncoder());
    private UuidGenerator uuidGenerator = new FasterXmlNoArgGeneratorAdapter(Generators.timeBasedEpochGenerator());

    private Object applicationEventPublisherCalledEvent;

    private final ApplicationEventPublisher applicationEventPublisher = new ApplicationEventPublisher() {
        @Override
        public void publishEvent(Object event) {
            applicationEventPublisherCalledEvent = event;
        }
    };

    private final AuthenticationTokenGenerator authenticationTokenGenerator = new AuthenticationTokenGenerator() {
        @Override
        public String create(BaseMember member) {
            return member.getId().toString();
        }
    };

    private MemberServiceImpl memberService = new MemberServiceImpl(memberRepository, passwordHasher, uuidGenerator, applicationEventPublisher, authenticationTokenGenerator);


    private MemberJoinDto givenMemberJoinDto;

    private Member existingMember;
    private String givenExistingPassword;

    @BeforeEach
    void init() {
        givenMemberJoinDto = new MemberJoinDto(randString(), randString());
        applicationEventPublisherCalledEvent = null;

        givenExistingPassword = randString() + randString();
        existingMember = Member.create(UUID.randomUUID(), randString(), givenExistingPassword, passwordHasher);
        memberRepository.save(existingMember);
    }

    @Test
    void join_event_should_be_published() {
        Member join = memberService.join(givenMemberJoinDto);

        assertThat(applicationEventPublisherCalledEvent)
                .isNotNull()
                .isInstanceOf(MemberJoinEvent.class);

        MemberJoinEvent calledMemberJoinEvent = (MemberJoinEvent) applicationEventPublisherCalledEvent;

        assertThat(tuple(calledMemberJoinEvent.memberId(), calledMemberJoinEvent.displayedName()))
                .isEqualTo(tuple(join.getId(), givenMemberJoinDto.username()));
    }

    @Test
    void authenticate_should_success() {
        // when
        MemberAuthDto memberAuthDto = new MemberAuthDto(existingMember.getUsername(), givenExistingPassword);
        String authenticate = memberService.authenticate(memberAuthDto);

        // then
        assertThat(authenticate)
                .isEqualTo(existingMember.getId().toString());
    }
    @Test
    void authenticate_should_fail_member_not_exists() {
        // when
        MemberAuthDto memberAuthDto = new MemberAuthDto(randString(), givenExistingPassword);
        // then
        assertThatThrownBy(() -> memberService.authenticate(memberAuthDto))
                .isInstanceOf(MemberNotFoundException.class);
    }
    @Test
    void authenticate_should_fail_password_mismatch() {
        // when
        MemberAuthDto memberAuthDto = new MemberAuthDto(existingMember.getUsername(), givenExistingPassword+"hi");

        // then
        assertThatThrownBy(() -> memberService.authenticate(memberAuthDto))
                .isInstanceOf(PasswordMismatchException.class);
    }
}