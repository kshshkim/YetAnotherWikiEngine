package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.PermissionLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthorityGrantData(
        UUID granterId,
        UUID granteeId,
        PermissionLevel permissionLevel,
        LocalDateTime expiresAt,
        String comment
) {
    public AuthorityGrantData {
        if (granterId == null) {
            throw new NullPointerException("granterId cannot be null");
        }
    }
}
