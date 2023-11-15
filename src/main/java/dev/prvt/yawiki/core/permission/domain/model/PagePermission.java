package dev.prvt.yawiki.core.permission.domain.model;

import dev.prvt.yawiki.core.permission.domain.PagePermissionUpdateValidator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;
import java.util.UUID;

@Getter
@Entity
@Table(name = "perm_page_permission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagePermission implements YawikiPermission {
    @Id
    @Column(name = "page_permission_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "namespace_permission_id")
    private NamespacePermission namespacePermission;

    @JoinColumn(name = "page_permission_permission_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Permission permission;

    /**
     * <p>페이지 개별 권한 레벨 반환.</p>
     */
    private Optional<PermissionLevel> getPageSpecificPermissionLevel(ActionType actionType) {
        return this.permission == null ?
                Optional.empty() :
                Optional.ofNullable(permission.getPermissionLevel(actionType));
    }

    /**
     * <p>페이지에 따로 설정된 것이 없으면 namespace 권한을 반환함.</p>
     */
    @Override
    public PermissionLevel getRequiredPermissionLevel(ActionType actionType) {
        return this.getPageSpecificPermissionLevel(actionType)
                .orElse(namespacePermission.getRequiredPermissionLevel(actionType));
    }

    public void updatePermission(Permission permission, PagePermissionUpdateValidator validator) {
        validatePermissionUpdate(permission, validator);
        this.permission = permission;
    }

    private void validatePermissionUpdate(Permission permission, PagePermissionUpdateValidator validator) {
        validator.validate(this, permission);
    }

    @Builder
    protected PagePermission(UUID id, NamespacePermission namespacePermission, Permission permission) {
        this.id = id;
        this.namespacePermission = namespacePermission;
        this.permission = permission;
    }
}
