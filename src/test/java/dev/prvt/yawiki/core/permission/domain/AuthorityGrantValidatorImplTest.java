package dev.prvt.yawiki.core.permission.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorityGrantValidatorImplTest {


    @Test
    @DisplayName("EVERYONE 권한은 부여 불가")
    void validate_everyone() {
        AuthorityGrantValidatorImpl authorityGrantValidator = new AuthorityGrantValidatorImpl();
        AuthorityProfile givenGranter = AuthorityProfile.builder()
                .grantedPermissions(List.of(GrantedPermission.builder().permissionLevel(PermissionLevel.ADMIN).build()))
                .build();
        AuthorityProfile givenGrantee = AuthorityProfile.builder()
                .build();

        assertThatThrownBy(() -> authorityGrantValidator.validate(givenGranter, givenGrantee, PermissionLevel.EVERYONE))
                .hasMessageContaining("cannot grant")
                .hasMessageContaining(PermissionLevel.EVERYONE.toString());
    }

    @Test
    @DisplayName("권한을 부여하려면 최소한 MANAGER 이상이어야함.")
    void validate_at_least_manager() {
        AuthorityGrantValidatorImpl authorityGrantValidator = new AuthorityGrantValidatorImpl();
        AuthorityProfile givenGranter = AuthorityProfile.builder()
                .grantedPermissions(List.of(GrantedPermission.builder().permissionLevel(PermissionLevel.ASSISTANT_MANAGER).build()))
                .build();
        AuthorityProfile givenGrantee = AuthorityProfile.builder()
                .build();

        assertThatThrownBy(() -> authorityGrantValidator.validate(givenGranter, givenGrantee, PermissionLevel.MEMBER))
                .describedAs("어떤 권한이 필요한지 메시지에 포함.")
                .hasMessageContaining("required permission level")
                .hasMessageContaining("minimum")
                .hasMessageContaining(PermissionLevel.MANAGER.toString())
        ;
    }

    @Test
    @DisplayName("자신보다 낮은 권한만 부여 가능")
    void validate_permission_level_should_be_lower_than_the_granter() {
        AuthorityGrantValidatorImpl authorityGrantValidator = new AuthorityGrantValidatorImpl();
        AuthorityProfile givenGranter = AuthorityProfile.builder()
                .grantedPermissions(List.of(GrantedPermission.builder().permissionLevel(PermissionLevel.MANAGER).build()))
                .build();
        AuthorityProfile givenGrantee = AuthorityProfile.builder()
                .build();

        assertThatThrownBy(() -> authorityGrantValidator.validate(givenGranter, givenGrantee, PermissionLevel.MANAGER))
                .describedAs("실패 사유 메시지에 포함.")
                .hasMessageContaining("granter should have higher permission level than")
                .hasMessageContaining(PermissionLevel.MANAGER.toString());
    }

    @Test
    @DisplayName("자신보다 더 높은 권한을 가진 대상에게 권한 부여 불가")
    void validate_granter_should_have_higher_permission_than_the_grantee() {
        AuthorityGrantValidatorImpl authorityGrantValidator = new AuthorityGrantValidatorImpl();
        AuthorityProfile givenGranter = AuthorityProfile.builder()
                .grantedPermissions(List.of(GrantedPermission.builder().permissionLevel(PermissionLevel.MANAGER).build()))
                .build();
        AuthorityProfile givenGrantee = AuthorityProfile.builder()
                .grantedPermissions(List.of(GrantedPermission.builder().permissionLevel(PermissionLevel.ADMIN).build()))
                .build();

        assertThatThrownBy(() -> authorityGrantValidator.validate(givenGranter, givenGrantee, PermissionLevel.ASSISTANT_MANAGER))
                .describedAs("실패 사유 메시지에 포함.")
                .hasMessageContaining("granter must have higher permission level than the grantee's")
                ;
    }
}