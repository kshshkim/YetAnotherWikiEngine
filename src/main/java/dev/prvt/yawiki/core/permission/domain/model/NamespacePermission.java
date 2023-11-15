package dev.prvt.yawiki.core.permission.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

// todo 커스텀 예외
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "perm_namespace_permission")
public class NamespacePermission implements YawikiPermission {
    @Id
    @Column(name = "namespace_id")
    private Integer namespaceId;

    @JoinColumn(name = "namespace_permission_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Permission permission;

    /**
     * 페이지에서 네임스페이스 권한보다 더 높은 권한을 요구하도록 설정 가능한지 여부.
     */
    private boolean upwardOverridable;
    /**
     * 페이지에서 네임스페이스 권한보다 더 낮은 권한을 요구하도록 설정 가능한지 여부.
     */
    private boolean downwardOverridable;
    
    @Override
    public PermissionLevel getRequiredPermissionLevel(ActionType actionType) {
        return this.permission.getPermissionLevel(actionType);
    }

    /**
     * <p>namespace에 적용되는 permission은 null값을 허용하지 않음.</p>
     * @param permission namespace의 기본 permission
     */
    void validateNamespacePermission(Permission permission) {
        boolean containsNull = Arrays.stream(ActionType.values())
                .map(permission::getPermissionLevel)
                .anyMatch(Objects::isNull);
        if (containsNull) {
            throw new IllegalStateException("namespace permission cannot contain null");
        }
    }

    /**
     * 주어진 permission에 포함된 모든 actionType-permissionLevel 쌍에 대해서, override 가능한지 검증함.
     */
    public void validatePermissionOverride(Permission permission) {
        if (!downwardOverridable && !upwardOverridable) {
            throw new IllegalStateException("override forbidden. namespaceId: " + namespaceId);
        }
        for (ActionType actionType: ActionType.values()) {
            validatePermissionOverride(actionType, permission.getPermissionLevel(actionType));
        }
    }

    /**
     * actionType에 해당하는 permissionLevel을 주어진 permissionLevel로 override 할 수 있는지 검증함.
     */
    void validatePermissionOverride(ActionType actionType, PermissionLevel permissionLevel) {
        if (permissionLevel == null) {
            return;
        }
        upwardOverrideCheck(permissionLevel, actionType);
        downwardOverrideCheck(permissionLevel, actionType);
    }

    /**
     * upwardOverride 가능한지 여부, 가능하지 않다면 기존에 설정된 권한보다 더 높은지 검증함.
     */
    private void upwardOverrideCheck(PermissionLevel permissionLevel, ActionType actionType) {
        PermissionLevel basePermissionLevel = permission.getPermissionLevel(actionType);
        if (!upwardOverridable && !basePermissionLevel.isHigherThanOrEqualTo(permissionLevel)) {
            throw new IllegalStateException("upward override forbidden. namespace permission level: " + basePermissionLevel + " invalid permission level: " + permissionLevel);
        }
    }

    /**
     * upwardOverride 가능한지 여부, 가능하지 않다면 기존에 설정된 권한보다 더 낮은지 검증함.
     */
    private void downwardOverrideCheck(PermissionLevel permissionLevel, ActionType actionType) {
        PermissionLevel basePermissionLevel = permission.getPermissionLevel(actionType);
        if (!downwardOverridable && !permissionLevel.isHigherThanOrEqualTo(basePermissionLevel)) {
            throw new IllegalStateException("downward override forbidden. namespace permission level: " + basePermissionLevel + " invalid permission level: " + permissionLevel);
        }
    }

    @Builder
    protected NamespacePermission(Integer namespaceId, Permission permission, Boolean upwardOverridable, Boolean downwardOverridable) {
        this.namespaceId = namespaceId;
        this.permission = permission;
        this.upwardOverridable = upwardOverridable;
        this.downwardOverridable = downwardOverridable;
        validateNamespacePermission(permission);
    }
}
