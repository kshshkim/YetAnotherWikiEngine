package dev.prvt.yawiki.core.permission.domain.model;

import dev.prvt.yawiki.common.util.jpa.uuid.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "perm_granted_permissions",
        indexes = {
                @Index(  // 외래키 제약조건 제거, 따로 인덱스 설정함
                        name = "idx__perm_granted_permission__grantee_id",
                        columnList = "grantee_id"
                ),
                @Index(  // 외래키 제약조건 제거, 따로 인덱스 설정함
                        name = "idx__perm_granted_permission__granter_id",
                        columnList = "granter_id"
                )
        }
)
public class GrantedPermission {
    @Id
    @UuidV7Generator
    @Column(name = "granted_permission_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grantee_id", updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private AuthorityProfile grantee;

    /**
     * 권한을 부여한 AuthorityProfile. null 인 경우 system
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granter_id", updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private AuthorityProfile granter;

    @Enumerated
    @Column(name = "permission_level")
    private PermissionLevel permissionLevel;

    private String comment;

    @Column(name = "granted_at")
    private LocalDateTime grantedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * expiresAt이 null 이면 무기한(false)
     * expiresAt이 null 아닐 경우 (현재시각 + expirationMargin분) 시점에 만료 여부 확인
     * @param expirationMargin 유효기간 종료 시점
     * @return 만료 여부
     */
    private boolean isExpired(long expirationMargin) {
        LocalDateTime now = LocalDateTime.now();
        return this.expiresAt != null &&
                this.expiresAt.isBefore(now.plusMinutes(expirationMargin));
    }

    /**
     * 현재 시점에는 만료되었는지 여부만 체크하지만, 수정될 수 있음.
     * @param expirationMargin 만료 판단 시점
     * @return 유효한 권한인지 여부
     */
    public boolean isValid(long expirationMargin) {
        return !isExpired(expirationMargin);
    }

    /**
     * AuthorityProfile 에서 호출함.
     * @param authorityProfile
     */
    void grantedTo(AuthorityProfile authorityProfile) {
        if (this.grantee == null) {
            this.grantee = authorityProfile;
        } else {
            throw new IllegalStateException("cannot update grantee");
        }
    }

    @Builder
    protected GrantedPermission(UUID id, AuthorityProfile grantee, AuthorityProfile granter, PermissionLevel permissionLevel, String comment, LocalDateTime grantedAt, LocalDateTime expiresAt) {
        this.id = id;
        this.grantee = grantee;
        this.granter = granter;
        this.permissionLevel = permissionLevel;
        this.comment = comment;
        this.grantedAt = grantedAt;
        this.expiresAt = expiresAt;
    }

    static public GrantedPermission create(AuthorityProfile granter, PermissionLevel permissionLevel, String comment, LocalDateTime expiresAt) {
        return GrantedPermission.builder()
                .granter(granter)
                .permissionLevel(permissionLevel)
                .comment(comment)
                .grantedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();
    }
}
