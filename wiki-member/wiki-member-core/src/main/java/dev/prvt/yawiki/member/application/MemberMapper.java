package dev.prvt.yawiki.member.application;

import dev.prvt.yawiki.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberData memberToMemberData(Member member) {
        return new MemberData(member.getId(), member.getUsername());
    }

}
