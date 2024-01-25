package dev.prvt.yawiki.auth.member.dto;

public record MemberTokenAuthDto(
        String username,
        String refreshToken
) {
}
