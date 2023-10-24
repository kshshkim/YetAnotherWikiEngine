package dev.prvt.yawiki.core.permission.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "perm_granted_permissions")
public class GrantedPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grantee_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private AuthorityProfile grantee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granter_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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

    @Builder
    protected GrantedPermission(Long id, AuthorityProfile grantee, AuthorityProfile granter, PermissionLevel permissionLevel, String comment, LocalDateTime grantedAt, LocalDateTime expiresAt) {
        this.id = id;
        this.grantee = grantee;
        this.granter = granter;
        this.permissionLevel = permissionLevel;
        this.comment = comment;
        this.grantedAt = grantedAt;
        this.expiresAt = expiresAt;
    }

    static public GrantedPermission create(AuthorityProfile granter, AuthorityProfile grantee, PermissionLevel permissionLevel, String comment, LocalDateTime expiresAt) {
        return GrantedPermission.builder()
                .grantee(grantee)
                .granter(granter)
                .permissionLevel(permissionLevel)
                .comment(comment)
                .grantedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();
    }
}
