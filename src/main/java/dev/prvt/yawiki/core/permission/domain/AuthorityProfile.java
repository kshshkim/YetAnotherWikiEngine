package dev.prvt.yawiki.core.permission.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "perm_authority_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
public class AuthorityProfile implements Persistable<UUID> {
    @Id
    @Column(name = "profile_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @CreatedDate
    @Column(name = "joined_date")
    private LocalDateTime joinedDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "grantee", cascade = CascadeType.PERSIST)
    private List<GrantedPermission> grantedPermissions = new ArrayList<>();

    /**
     * <p>now + margin minutes 시점에 만료되지 않은 권한 중, 가장 높은 권한을 반환함.</p>
     * @param expirationMargin margin in minutes
     * @return 가장 높은 권한
     */
    public PermissionLevel getMaxPermissionLevel(long expirationMargin) {
        return grantedPermissions.stream()
                .filter(grantedPermission -> grantedPermission.isValid(expirationMargin))
                .map(GrantedPermission::getPermissionLevel)
                .max(Comparator.comparing(PermissionLevel::getIntValue))
                .orElse(PermissionLevel.EVERYONE);
    }

    /**
     * OneToMany 동기화, persist를 위해 호출되어야함. 오직 해당 클래스에서만 호출함.
     * @param grantedPermission 이 엔티티의 grantee 는 해당 엔티티와 동일해야함.
     */
    void addPermission(GrantedPermission grantedPermission) {
        this.grantedPermissions.add(grantedPermission);
        grantedPermission.grantedTo(this);
    }

    /**
     * 다른 AuthorityProfile 에 권한을 부여함.
     * @param grantee 권한을 부여받을 다른 AuthorityProfile
     * @param permissionLevelToGrant 부여할 권한 수준
     * @param comment 권한 부여 사유 등 요약
     * @param expiresAt 만료 기한. null 일시 무제한
     */
    public void grantPermissionTo(AuthorityProfile grantee, PermissionLevel permissionLevelToGrant, String comment, LocalDateTime expiresAt, AuthorityGrantValidator validator) {
        validator.validate(this, grantee, permissionLevelToGrant);
        grantee.addPermission(GrantedPermission.create(this, permissionLevelToGrant, comment, expiresAt));
    }

    @Builder
    protected AuthorityProfile(UUID id, List<GrantedPermission> grantedPermissions, GrantedPermission grantedPermission) {
        this.id = id;
        if (grantedPermissions != null) {
            grantedPermissions.forEach(this::addPermission);
        }
        if (grantedPermission != null) {
            this.addPermission(grantedPermission);
        }
    }

    static public AuthorityProfile create(UUID id) {
        return create(id, PermissionLevel.NEW_MEMBER);
    }

    static public AuthorityProfile create(UUID id, PermissionLevel defaultPermissionLevel) {
        GrantedPermission grantedPermission = GrantedPermission.create(
                null,
                defaultPermissionLevel,
                "default permission level",
                null
        );
        return create(id, grantedPermission);
    }

    static public AuthorityProfile create(UUID id, GrantedPermission grantedPermission) {
        return AuthorityProfile.builder()
                .id(id)
                .grantedPermission(grantedPermission)
                .build();
    }

    @Override
    public boolean isNew() {
        return this.joinedDate == null;
    }
}
