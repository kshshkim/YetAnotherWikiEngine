package dev.prvt.yawiki.auth.member.application;

import dev.prvt.yawiki.auth.jwt.application.JwtIssuer;
import dev.prvt.yawiki.auth.jwt.domain.AuthToken;
import dev.prvt.yawiki.auth.member.domain.Member;
import dev.prvt.yawiki.auth.member.domain.MemberRepository;
import dev.prvt.yawiki.auth.member.domain.PasswordHasher;
import dev.prvt.yawiki.auth.member.dto.MemberJoinDto;
import dev.prvt.yawiki.auth.member.dto.MemberPasswordAuthDto;
import dev.prvt.yawiki.auth.member.dto.MemberTokenAuthDto;
import dev.prvt.yawiki.auth.member.exception.MemberNotFoundException;
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
    private final JwtIssuer jwtIssuer;

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
    public AuthToken authenticate(MemberPasswordAuthDto memberPasswordAuthDto) {
        Member member = memberRepository.findByUsername(memberPasswordAuthDto.username())
                .orElseThrow(MemberNotFoundException::new);
        member.validatePassword(memberPasswordAuthDto.password(), passwordHasher);
        return jwtIssuer.issue(member.getId(), member.getUsername());
    }

    @Override
    public AuthToken authenticate(MemberTokenAuthDto memberTokenAuthDto) {
        return jwtIssuer.renew(memberTokenAuthDto.refreshToken(), memberTokenAuthDto.username());
    }
}
