package dev.prvt.yawiki.core.permission.domain.model;

import dev.prvt.yawiki.core.permission.domain.AuthorityGrantValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorityProfileTest {
    @Mock
    AuthorityGrantValidator mockValidator;
    @Captor
    ArgumentCaptor<AuthorityProfile> granterCaptor;
    @Captor
    ArgumentCaptor<AuthorityProfile> granteeCaptor;
    @Captor
    ArgumentCaptor<PermissionLevel> permissionLevelCaptor;
    @Mock
    GrantedPermission mockGrantedPermission;

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

    @Test
    void create_with_id() {
        // given
        UUID given = UUID.randomUUID();

        // when
        AuthorityProfile authorityProfile = AuthorityProfile.create(given);

        // then
        List<GrantedPermission> grantedPermissions = authorityProfile.getGrantedPermissions();
        assertThat(grantedPermissions)
                .describedAs("기본 권한이 생성돼야함.")
                .isNotEmpty()
                .hasSize(1);

        assertThat(authorityProfile.getMaxPermissionLevel(0))
                .describedAs("기본값은 신규 회원")
                .isEqualTo(PermissionLevel.NEW_MEMBER);
    }

    @Test
    void create_with_id_and_permissionLevel() {
        // given
        UUID givenId = UUID.randomUUID();
        PermissionLevel givenPermissionLevel = PermissionLevel.MEMBER;

        // when
        AuthorityProfile authorityProfile = AuthorityProfile.create(givenId, givenPermissionLevel);

        // then
        assertThat(authorityProfile.getMaxPermissionLevel(0))
                .describedAs("지정한 권한 레벨으로 설정돼야함.")
                .isEqualTo(PermissionLevel.MEMBER);
    }

    @Test
    void create_with_id_and_pre_built_granted_permission() {
        // given
        UUID givenId = UUID.randomUUID();
        AuthorityProfile authorityProfile = AuthorityProfile.create(givenId, mockGrantedPermission);
        PermissionLevel givenPermissionLevel = PermissionLevel.NEW_MEMBER;
        given(mockGrantedPermission.getPermissionLevel())
                .willReturn(givenPermissionLevel);
        given(mockGrantedPermission.isValid(anyLong()))
                .willReturn(true);

        // when
        List<GrantedPermission> grantedPermissions = authorityProfile.getGrantedPermissions();

        // then
        assertThat(grantedPermissions)
                .describedAs("기본 권한이 생성돼야함.")
                .isNotEmpty()
                .hasSize(1);

        assertThat(authorityProfile.getMaxPermissionLevel(0))
                .describedAs("제공된 granted permission 권한 수준은 NEW_MEMBER")
                .isEqualTo(givenPermissionLevel);
    }
}