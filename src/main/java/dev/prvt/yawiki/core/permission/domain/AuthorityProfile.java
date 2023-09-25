package dev.prvt.yawiki.core.permission.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * <p>문서 수정 작업 등, 애플리케이션에 접근하는 행위자들의 권한 프로필.</p>
 */
@Entity
@Getter
@Table(name = "authority_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorityProfile {
    @Id
    @Column(name = "profile_id", columnDefinition = "BINARY(16)")
    private UUID id;  // should match with actor's id

    /**
     * AuthorityProfile 이 부여받은 그룹 권한. 프로필 생성시 기본 권한도 부여하는 것을 가정했음.
     */
    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<GrantedGroupAuthority> groupAuthorities = new ArrayList<>();

    void addGroupAuthority(GrantedGroupAuthority groupAuthority) {
        groupAuthorities.add(groupAuthority);
    }

    protected AuthorityProfile(UUID id) {
        this.id = id;
    }

    public static AuthorityProfile createWithGroup(UUID id, PermissionGroup permissionGroup, int authorityLevel) {
        AuthorityProfile created = new AuthorityProfile(id);
        GrantedGroupAuthority.builder()
                .group(permissionGroup)
                .authorityLevel(authorityLevel)
                .profile(created)
                .build();
        return created;
    }
}
