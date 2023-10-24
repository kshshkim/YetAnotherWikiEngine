package dev.prvt.yawiki.core.permission.domain;

public interface YawikiPermission {
    PermissionLevel getRequiredPermissionLevel(ActionType actionType);
}
