package dev.prvt.yawiki.core.permission.domain.model;

import lombok.Builder;

public record PermissionData(
        String description,
        PermissionLevel create,
        PermissionLevel editCommit,
        PermissionLevel editRequest,
        PermissionLevel delete,
        PermissionLevel rename,
        PermissionLevel discussionCreate,
        PermissionLevel discussionParticipate
) {
    @Builder
    public PermissionData {
    }
}
