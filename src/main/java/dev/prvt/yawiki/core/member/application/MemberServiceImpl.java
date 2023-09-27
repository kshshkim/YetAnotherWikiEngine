package dev.prvt.yawiki.core.member.application;

import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.core.member.domain.Member;
import dev.prvt.yawiki.core.member.domain.MemberException;
import dev.prvt.yawiki.core.member.domain.MemberRepository;
import dev.prvt.yawiki.core.member.domain.PasswordHasher;
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
}
