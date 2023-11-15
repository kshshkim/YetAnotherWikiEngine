package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.model.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;

public interface AuthorityGrantValidator {
    /**
     * 권한 부여가 가능한지 검증함.
     * <li>EVERYONE 권한은 부여 불가능함.</li>
     * <li>권한을 부여하는 자의 최소 요구 권한은 MANAGER.</li>
     * <li>자신의 최고 권한보다 낮은 권한만 부여 가능함.</li>
     * @param granter 권한을 부여하려는 AuthorityProfile
     * @param grantee 부여받는 AuthorityProfile
     * @param permissionLevelToGrant 부여하려는 권한
     */
    void validate(AuthorityProfile granter, AuthorityProfile grantee, PermissionLevel permissionLevelToGrant);
}
