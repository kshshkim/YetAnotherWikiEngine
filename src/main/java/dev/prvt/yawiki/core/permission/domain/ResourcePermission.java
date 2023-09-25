package dev.prvt.yawiki.core.permission.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.UUID;

/**
 * <p>서비스 전반에 존재하는 자원에 대한 ACL 할당 정보. 당장은 WikiPage 에만 적용됨.</p>
 * <p>우선 순위 1. requiredPermission 2. ownerGroup 의 기본 acl 3. 전역 acl 설정</p>
 */
@Entity
@Getter
@Table(name = "permission_resource")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourcePermission {
    @Id
    @Column(name = "permission_resource_id", columnDefinition = "BINARY(16)")
    private UUID id;  // should match with resource id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_group_id", nullable = false)
    private PermissionGroup ownerGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acl_id")
    private Permission specificPermission;  // can be null. if null, check the ownerGroup.defaultResourcePermission

    @NotNull
    public Permission getRequiredPermission() {
        return specificPermission == null ? ownerGroup.getDefaultResourcePermission() :
                specificPermission;
    }

    public void updatePermission(Permission updatedPermission) {
        this.specificPermission = updatedPermission;
    }

    public ResourcePermission(UUID id, PermissionGroup ownerGroup, Permission specificPermission) {
        this.id = id;
        this.ownerGroup = ownerGroup;
        this.specificPermission = specificPermission;
    }
}
