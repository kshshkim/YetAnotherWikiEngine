package dev.prvt.yawiki.core.permission.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorityProfileTest {
    @Test
    void getMaxPermissionLevel_should_return_the_highest_permission() {
        // given
        GrantedPermission memberPermission = GrantedPermission.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .permissionLevel(PermissionLevel.MEMBER)
                .build();
        GrantedPermission managerPermission = GrantedPermission.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .permissionLevel(PermissionLevel.MANAGER)
                .build();

        AuthorityProfile givenAuthorityProfile = AuthorityProfile.builder()
                .grantedPermissions(List.of(memberPermission, managerPermission))
                .build();

        // when
        PermissionLevel result = givenAuthorityProfile.getMaxPermissionLevel(0);

        // then
        assertThat(result).isEqualTo(PermissionLevel.MANAGER);
    }

    @Test
    void getMaxPermissionLevel_should_return_the_highest_permission_among_valid_permissions() {
        // given
        GrantedPermission memberPermission = GrantedPermission.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .permissionLevel(PermissionLevel.MEMBER)
                .build();
        GrantedPermission managerPermission = GrantedPermission.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .permissionLevel(PermissionLevel.MANAGER)
                .build();
        GrantedPermission invalidPermission = GrantedPermission.builder()
                .expiresAt(LocalDateTime.now().minusMinutes(3))
                .permissionLevel(PermissionLevel.ADMIN)
                .build();

        AuthorityProfile givenAuthorityProfile = AuthorityProfile.builder()
                .grantedPermissions(List.of(memberPermission, managerPermission, invalidPermission))
                .build();

        // when
        PermissionLevel result = givenAuthorityProfile.getMaxPermissionLevel(0);

        // then
        assertThat(result)
                .describedAs("isValid 가 유효하다는 값을 반환한 권한 중, 가장 높은 권한인 MANAGER를 반환해야함.")
                .isNotEqualTo(PermissionLevel.ADMIN)
                .isEqualTo(PermissionLevel.MANAGER);
    }

    @Test
    void addPermission() {
        AuthorityProfile given = AuthorityProfile.builder()
                .grantedPermissions(null)
                .build();

        GrantedPermission givenGrantedPermission = GrantedPermission.builder()
                .build();

        // when
        given.addPermission(givenGrantedPermission);

        // then
        boolean added = given.getGrantedPermissions().stream()
                .anyMatch(gp -> gp == givenGrantedPermission);
        assertThat(added).isTrue();
    }

    @Mock
    AuthorityGrantValidator mockValidator;

    @Captor
    ArgumentCaptor<AuthorityProfile> granterCaptor;
    @Captor
    ArgumentCaptor<AuthorityProfile> granteeCaptor;
    @Captor
    ArgumentCaptor<PermissionLevel> permissionLevelCaptor;
    @Test
    void grantPermissionTo_should_call_validator() {
        AuthorityProfile granter = AuthorityProfile.builder().build();
        AuthorityProfile grantee = AuthorityProfile.builder().build();
        PermissionLevel givenPermissionLevel = PermissionLevel.ASSISTANT_MANAGER;

        // when
        granter.grantPermissionTo(grantee, givenPermissionLevel, randString(), LocalDateTime.now().plusMinutes(10), mockValidator);

        // then
        verify(mockValidator).validate(granterCaptor.capture(), granteeCaptor.capture(), permissionLevelCaptor.capture());
        assertThat(granterCaptor.getValue())
                .describedAs("granter 인자 제대로 넘어가는지 체크")
                .isSameAs(granter);
        assertThat(granteeCaptor.getValue())
                .describedAs("grantee 인자 제대로 넘어가는지 체크")
                .isSameAs(grantee);
        assertThat(permissionLevelCaptor.getValue())
                .describedAs("권한 수준 인자 제대로 넘어가는지 체크")
                .isSameAs(givenPermissionLevel);
    }

    @Test
    void grantPermissionTo_should_add_GrantedAuthority_to_grantee() {
        AuthorityProfile granter = AuthorityProfile.builder().build();
        AuthorityProfile grantee = AuthorityProfile.builder().build();
        PermissionLevel givenPermissionLevel = PermissionLevel.ASSISTANT_MANAGER;

        // when
        granter.grantPermissionTo(grantee, givenPermissionLevel, randString(), LocalDateTime.now().plusMinutes(10), mockValidator);

        // then
        assertThat(granter.getGrantedPermissions())
                .describedAs("granter에는 권한이 추가되어선 안 됨.(기본 권한을 가지고 있도록 구현할 경우 테스트 실패 가능성 있음.)")
                .isEmpty();

        Optional<GrantedPermission> addedPermission = grantee.getGrantedPermissions().stream()
                .findAny();

        assertThat(addedPermission)
                .describedAs("grantee 에게 권한이 적절히 추가되어야함.")
                .isPresent();

        GrantedPermission presentPermission = addedPermission.orElseThrow();

        assertThat(presentPermission.getGrantee())
                .describedAs("grantee 가 적절히 설정됨.")
                .isEqualTo(grantee);

        assertThat(presentPermission.getGranter())
                .describedAs("granter 가 적절히 설정됨.")
                .isEqualTo(granter);
    }
}