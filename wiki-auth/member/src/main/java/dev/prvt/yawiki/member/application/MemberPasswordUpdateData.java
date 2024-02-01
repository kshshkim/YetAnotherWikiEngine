package dev.prvt.yawiki.member.application;

public record MemberPasswordUpdateData(
    String username,
    String oldPassword,
    String newPassword
) {

}
