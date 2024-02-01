package dev.prvt.yawiki.member.application;

import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.member.domain.Member;
import dev.prvt.yawiki.member.domain.MemberRepository;
import dev.prvt.yawiki.member.domain.PasswordHasher;
import dev.prvt.yawiki.member.exception.NoSuchMemberException;
import java.util.UUID;
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
    private final MemberMapper memberMapper;

    @Override
    public MemberData join(MemberJoinData data) {
        Member joined = memberRepository.save(Member.create(
            uuidGenerator.generate(),
            data.username(),
            data.password(),
            passwordHasher)
        );
        return memberMapper.memberToMemberData(joined);
    }

    @Override
    @Transactional(readOnly = true)
    public UUID verifyPassword(MemberPasswordVerificationData data) {
        Member found = memberRepository.findByUsername(data.username())
                            .orElseThrow(NoSuchMemberException::new);

        found.verifyPassword(data.password(), passwordHasher);
        return found.getId();
    }

    @Override
    public void updatePassword(MemberPasswordUpdateData data) {
        Member found = memberRepository.findByUsername(data.username())
                            .orElseThrow(NoSuchMemberException::new);
        found.updatePassword(data.oldPassword(), data.newPassword(), passwordHasher);
    }

}
