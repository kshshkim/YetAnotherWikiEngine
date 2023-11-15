package dev.prvt.yawiki.core.permission.domain.model;

import lombok.Getter;

/**
 * 권한 레벨. 변동시 유연하게 대처 가능하도록 여유를 줬음.
 */
@Getter
public enum PermissionLevel {
    EVERYONE(0, "EVERYONE"),
    NEW_MEMBER(5, "NEW_MEMBER"),
    MEMBER(10, "MEMBER"),
    ASSISTANT_MANAGER(20, "ASSISTANT_MANAGER"),
    MANAGER(30, "MANAGER"),
    ADMIN(40, "ADMIN");

    private final int intValue;
    private final String stringValue;

    PermissionLevel(int intValue, String stringValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
    }

    public boolean isHigherThanOrEqualTo(PermissionLevel permissionLevel) {
        return permissionLevel.intValue <= this.intValue;
    }
}
