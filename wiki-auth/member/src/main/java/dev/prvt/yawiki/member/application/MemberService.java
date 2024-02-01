package dev.prvt.yawiki.member.application;


import dev.prvt.yawiki.member.exception.NoSuchMemberException;
import dev.prvt.yawiki.member.exception.PasswordMismatchException;
import java.util.UUID;

public interface MemberService {

    MemberData join(MemberJoinData data);

    /**
     * 비밀번호가 일치하면 회원 ID 반환.
     * @return (username, password) 튜플이 일치하는 회원의 ID
     */
    UUID verifyPassword(MemberPasswordVerificationData data) throws PasswordMismatchException, NoSuchMemberException;

    void updatePassword(MemberPasswordUpdateData data) throws PasswordMismatchException, NoSuchMemberException;

}
