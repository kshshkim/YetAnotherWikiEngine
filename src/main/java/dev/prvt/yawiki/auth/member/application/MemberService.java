package dev.prvt.yawiki.auth.member.application;

import dev.prvt.yawiki.auth.jwt.domain.AuthToken;
import dev.prvt.yawiki.auth.member.domain.Member;
import dev.prvt.yawiki.auth.member.dto.MemberJoinDto;
import dev.prvt.yawiki.auth.member.dto.MemberPasswordAuthDto;
import dev.prvt.yawiki.auth.member.dto.MemberTokenAuthDto;

public interface MemberService {
    Member join(MemberJoinDto memberJoinDto);
    AuthToken authenticate(MemberPasswordAuthDto memberPasswordAuthDto);
    AuthToken authenticate(MemberTokenAuthDto memberTokenAuthDto);
}
