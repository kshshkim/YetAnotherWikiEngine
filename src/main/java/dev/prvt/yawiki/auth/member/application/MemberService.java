package dev.prvt.yawiki.auth.member.application;

import dev.prvt.yawiki.auth.member.domain.Member;

public interface MemberService {
    Member join(MemberJoinDto memberJoinDto);
    String authenticate(MemberAuthDto memberAuthDto);
}
