package dev.prvt.yawiki.core.permission.application;

import java.util.UUID;

/**
 * <li>AuthorityProfile 생성</li>
 * <li>GrantedGroupAuthority 부여</li>
 */
public interface AuthorityProfileCommandService {
    /**
     * 권한 프로필 생성
     * @param contributorId
     */
    void createAuthorityProfile(UUID contributorId);

    /**
     * 권한 부여
     * @param authorityGrantData
     */
    void grantAuthority(AuthorityGrantData authorityGrantData);
}
