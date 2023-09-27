package dev.prvt.yawiki.core.member.application;

import com.fasterxml.uuid.Generators;
import dev.prvt.yawiki.common.uuid.FasterXmlNoArgGeneratorAdapter;
import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.core.member.domain.Member;
import dev.prvt.yawiki.core.member.domain.MemberMemoryRepository;
import dev.prvt.yawiki.core.member.domain.MemberRepository;
import dev.prvt.yawiki.core.member.domain.PasswordHasher;
import dev.prvt.yawiki.core.member.infra.PasswordHasherImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class MemberServiceImplTest {
    private MemberRepository memberRepository = new MemberMemoryRepository();
    private PasswordHasher passwordHasher = new PasswordHasherImpl(new BCryptPasswordEncoder());
    private UuidGenerator uuidGenerator = new FasterXmlNoArgGeneratorAdapter(Generators.timeBasedEpochGenerator());

    private Object calledEvent;

    private ApplicationEventPublisher applicationEventPublisher = new ApplicationEventPublisher() {
        @Override
        public void publishEvent(Object event) {
            calledEvent = event;
        }
    };
    private MemberServiceImpl memberService = new MemberServiceImpl(memberRepository, passwordHasher, uuidGenerator, applicationEventPublisher);


    private MemberJoinDto givenMemberJoinDto;

    @BeforeEach
    void init() {
        givenMemberJoinDto = new MemberJoinDto(randString(), randString());
        calledEvent = null;
    }

    @Test
    void join_event_should_be_published() {
        Member join = memberService.join(givenMemberJoinDto);

        assertThat(calledEvent)
                .isNotNull()
                .isInstanceOf(MemberJoinEvent.class);

        MemberJoinEvent calledMemberJoinEvent = (MemberJoinEvent) calledEvent;

        assertThat(tuple(calledMemberJoinEvent.memberId(), calledMemberJoinEvent.displayedName()))
                .isEqualTo(tuple(join.getId(), givenMemberJoinDto.username()));
    }
}