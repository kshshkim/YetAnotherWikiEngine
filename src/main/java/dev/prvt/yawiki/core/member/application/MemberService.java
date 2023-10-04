package dev.prvt.yawiki.core.member.application;

import dev.prvt.yawiki.core.member.domain.Member;

public interface MemberService {
    Member join(MemberJoinDto memberJoinDto);
    String authenticate(MemberAuthDto memberAuthDto);
}
