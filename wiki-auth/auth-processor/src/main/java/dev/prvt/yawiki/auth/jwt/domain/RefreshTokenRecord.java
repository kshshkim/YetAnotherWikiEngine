package dev.prvt.yawiki.auth.jwt.domain;

import java.util.UUID;

public record RefreshTokenRecord(
        String refreshToken,
        UUID subjectId,
        String subjectName
) {
}
