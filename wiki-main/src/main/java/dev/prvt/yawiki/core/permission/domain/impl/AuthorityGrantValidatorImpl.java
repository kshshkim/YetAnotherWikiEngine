package dev.prvt.yawiki.core.permission.domain.impl;

import dev.prvt.yawiki.core.permission.domain.AuthorityGrantValidator;
import dev.prvt.yawiki.core.permission.domain.model.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import org.springframework.stereotype.Component;

@Component
public class AuthorityGrantValidatorImpl implements AuthorityGrantValidator {
    @Override
    public void validate(AuthorityProfile granter, AuthorityProfile grantee, PermissionLevel permissionLevelToGrant) {
        // EVERYONE 권한은 부여 불가능함. todo property화
        if (permissionLevelToGrant == PermissionLevel.EVERYONE) {
            throw new IllegalStateException("cannot grant permissionLevel: " + PermissionLevel.EVERYONE);
        }
        // 권한을 부여하는 자의 권한 수준
        PermissionLevel granterPermissionLevel = granter.getMaxPermissionLevel(0);
        // 권한을 부여받는 자의 권한 수준
        PermissionLevel granteePermissionLevel = grantee.getMaxPermissionLevel(0);

        // MANAGER 이상이어야 권한 부여 가능 todo property화
        if (!granterPermissionLevel.isHigherThanOrEqualTo(PermissionLevel.MANAGER)) {
            throw new IllegalStateException("cannot grant permission. required permission level minimum: " + PermissionLevel.MANAGER);
        }
        // 자신의 권한보다 낮은 권한만 부여 가능
        if (permissionLevelToGrant.isHigherThanOrEqualTo(granterPermissionLevel)){
            throw new IllegalStateException("cannot grant permission. granter should have higher permission level than " + permissionLevelToGrant);
        }
        // 자신보다 높거나 동일한 권한을 가진 계정에는 권한 부여 불가
        if (granteePermissionLevel.isHigherThanOrEqualTo(granterPermissionLevel)) {
            throw new IllegalStateException("cannot grant permission. granter must have higher permission level than the grantee's.");
        }
    }
}
