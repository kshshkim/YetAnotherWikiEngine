package dev.prvt.yawiki.member.application;

import dev.prvt.yawiki.member.domain.Member;
import dev.prvt.yawiki.member.dto.MemberJoinDto;

public interface MemberService {
    Member join(MemberJoinDto memberJoinDto);
}
