package dev.prvt.yawiki.member.application;

import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.member.domain.Member;
import dev.prvt.yawiki.member.domain.MemberRepository;
import dev.prvt.yawiki.member.domain.PasswordHasher;
import dev.prvt.yawiki.member.dto.MemberJoinDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordHasher passwordHasher;
    private final UuidGenerator uuidGenerator;

    @Override
    public Member join(MemberJoinDto memberJoinDto) {
        Member joined = memberRepository.save(Member.create(
                uuidGenerator.generate(),
                memberJoinDto.username(),
                memberJoinDto.password(),
                passwordHasher)
        );
        return joined;
    }
}
