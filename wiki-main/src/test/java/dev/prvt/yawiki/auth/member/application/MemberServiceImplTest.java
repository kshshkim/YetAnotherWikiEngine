package dev.prvt.yawiki.auth.member.application;

import com.fasterxml.uuid.Generators;
import dev.prvt.yawiki.auth.jwt.application.JwtIssuer;
import dev.prvt.yawiki.auth.jwt.domain.AuthToken;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenExpirationException;
import dev.prvt.yawiki.auth.member.domain.*;
import dev.prvt.yawiki.auth.member.dto.MemberJoinDto;
import dev.prvt.yawiki.auth.member.dto.MemberPasswordAuthDto;
import dev.prvt.yawiki.auth.member.dto.MemberTokenAuthDto;
import dev.prvt.yawiki.auth.member.exception.MemberNotFoundException;
import dev.prvt.yawiki.auth.member.exception.PasswordMismatchException;
import dev.prvt.yawiki.auth.member.infra.PasswordHasherImpl;
import dev.prvt.yawiki.common.uuid.FasterXmlNoArgGeneratorAdapter;
import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.core.event.MemberJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static dev.prvt.yawiki.common.testutil.Fixture.randString;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @Mock
    private JwtIssuer jwtIssuer;

    private MemberServiceImpl memberService;


    private MemberJoinDto givenMemberJoinDto;

    private Member existingMember;
    private String givenExistingPassword;

    private AuthToken expectingAuthToken;

    @BeforeEach
    void init() {
        givenMemberJoinDto = new MemberJoinDto(randString(), randString());
        applicationEventPublisherCalledEvent = null;

        givenExistingPassword = randString() + randString();
        existingMember = Member.create(UUID.randomUUID(), randString(), givenExistingPassword, passwordHasher);
        memberRepository.save(existingMember);

        expectingAuthToken = new AuthToken(UUID.randomUUID().toString() + UUID.randomUUID().toString(), UUID.randomUUID().toString());
        memberService = new MemberServiceImpl(memberRepository, passwordHasher, uuidGenerator, applicationEventPublisher, jwtIssuer);
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
        when(jwtIssuer.issue(existingMember.getId(), existingMember.getUsername()))
                .thenReturn(expectingAuthToken);

        // when
        MemberPasswordAuthDto memberPasswordAuthDto = new MemberPasswordAuthDto(existingMember.getUsername(), givenExistingPassword);
        AuthToken authenticate = memberService.authenticate(memberPasswordAuthDto);

        // then
        assertThat(authenticate)
                .isNotNull()
                .isEqualTo(expectingAuthToken);
    }

    @Test
    void authenticate_should_fail_member_not_exists() {
        // when
        MemberPasswordAuthDto memberPasswordAuthDto = new MemberPasswordAuthDto(randString(), givenExistingPassword);
        // then
        assertThatThrownBy(() -> memberService.authenticate(memberPasswordAuthDto))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void authenticate_should_fail_password_mismatch() {
        // when
        MemberPasswordAuthDto memberPasswordAuthDto = new MemberPasswordAuthDto(existingMember.getUsername(), givenExistingPassword+"hi");

        // then
        assertThatThrownBy(() -> memberService.authenticate(memberPasswordAuthDto))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    void authenticate_with_RefreshToken() {
        // given
        String givenRefreshToken = UUID.randomUUID().toString();
        when(jwtIssuer.renew(givenRefreshToken, existingMember.getUsername()))
                .thenReturn(expectingAuthToken);

        // when
        MemberTokenAuthDto memberTokenAuthDto = new MemberTokenAuthDto(existingMember.getUsername(), givenRefreshToken);
        AuthToken authenticate = memberService.authenticate(memberTokenAuthDto);

        // then
        assertThat(authenticate)
                .isNotNull()
                .isEqualTo(expectingAuthToken);
    }

    @Test
    void authenticate_with_refresh_token_exception_test() {
        String notFoundTrigger = UUID.randomUUID().toString();
        String expiredTrigger = UUID.randomUUID().toString();

        when(jwtIssuer.renew(notFoundTrigger, existingMember.getUsername()))
                .thenThrow(new RuntimeException("token not found"));
        when(jwtIssuer.renew(expiredTrigger, existingMember.getUsername()))
                .thenThrow(RefreshTokenExpirationException.INSTANCE);

        // when then
        assertThatThrownBy(() -> memberService.authenticate(new MemberTokenAuthDto(existingMember.getUsername(), notFoundTrigger)))
                .hasMessageContaining("not found");
        assertThatThrownBy(() -> memberService.authenticate(new MemberTokenAuthDto(existingMember.getUsername(), expiredTrigger)))
                .isInstanceOf(RefreshTokenExpirationException.class);
    }
}