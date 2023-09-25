package dev.prvt.yawiki.core.permission.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

import static dev.prvt.yawiki.common.uuid.Const.UUID_V7;

/**
 * AuthorityProfile 에 부여된 그룹 권한.
 */
@Entity
@Getter
@Table(
        name = "granted_group_authority",
        indexes = {
                @Index(
                        name = "idx__granted_group_authority__profile_id__group_id",
                        columnList = "profile_id, group_id",
                        unique = true
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GrantedGroupAuthority {
    @Id
    @GeneratedValue(generator = "uuid-v7")
    @GenericGenerator(name = "uuid-v7", strategy = UUID_V7)
    @Column(name = "granted_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false, nullable = false)
    private AuthorityProfile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", updatable = false, nullable = false)
    private PermissionGroup group;

    /**
     * 1 - member
     * 2 - manager
     * 3 - admin
     */
    private int authorityLevel;

    @Builder
    protected GrantedGroupAuthority(AuthorityProfile profile, PermissionGroup group, int authorityLevel) {
        this.profile = profile;
        this.group = group;
        this.authorityLevel = authorityLevel;

        if (this.profile == null) {
            throw new NullPointerException("profile must not be null");
        }

        if (this.group == null) {
            throw new NullPointerException("group must not be null");
        }
        this.profile.addGroupAuthority(this);
    }
}
