package dev.prvt.yawiki.core.permission.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 */

@Entity
@Getter
@Table(name = "permissions_group")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PermissionGroup {
    @Id
    @Column(name = "group_id", columnDefinition = "BINARY(16)")
    private UUID id;
    private String name;

    /**
     * <p>
     *     이 PermissionGroup 을 소유자로 하는 문서들이 기본적으로 가지는 권한. ResourcePermission 엔티티가 존재하지 않는 경우 이를 참조함.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)  // Permission 생성 간소화
    @JoinColumn(name = "default_resource_permission_id", nullable = false)
    private Permission defaultResourcePermission;

    public PermissionGroup(UUID id, String name, Permission defaultResourcePermission) {
        this.id = id;
        this.name = name;
        this.defaultResourcePermission = defaultResourcePermission;
    }
}
