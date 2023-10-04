package dev.prvt.yawiki.auth.member.application;

import dev.prvt.yawiki.auth.member.domain.*;
import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.core.event.MemberJoinEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordHasher passwordHasher;
    private final UuidGenerator uuidGenerator;
    private final ApplicationEventPublisher publisher;
    private final AuthenticationTokenGenerator authenticationTokenGenerator;

    @Override
    public Member join(MemberJoinDto memberJoinDto) {
        Member joined = memberRepository.save(Member.create(
                uuidGenerator.generate(),
                memberJoinDto.username(),
                memberJoinDto.password(),
                passwordHasher)
        );
        publisher.publishEvent(new MemberJoinEvent(joined.getId(), joined.getDisplayedName()));
        return joined;
    }

    @Override
    public String authenticate(MemberAuthDto memberAuthDto) {
        Member member = memberRepository.findByUsername(memberAuthDto.username())
                .orElseThrow(MemberNotFoundException::new);
        member.validatePassword(memberAuthDto.password(), passwordHasher);
        return authenticationTokenGenerator.create(member);
    }
}
