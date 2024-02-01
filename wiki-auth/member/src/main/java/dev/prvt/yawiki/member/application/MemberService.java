package dev.prvt.yawiki.member.application;


public interface MemberService {

    MemberData join(MemberJoinData memberJoinData);

    void verifyPassword(MemberPasswordVerificationData memberPasswordVerificationData);

    void updatePassword(MemberPasswordUpdateData memberPasswordUpdateData);
}
