package dev.prvt.yawiki.auth.jwt.domain;

public record AuthToken(
        String accessToken,
        String refreshToken
) {
}
