package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.fixture.Fixture;
import dev.prvt.yawiki.fixture.PermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AuthorityProfileTest {
    Permission givenPermission;
    PermissionGroup givenPermissionGroup;
    @BeforeEach
    void init() {
        givenPermission = PermissionFixture.aPermission().build();
        givenPermissionGroup = new PermissionGroup(UUID.randomUUID(), Fixture.randString(), givenPermission);
    }

    @Test
    void createWithGroup() {
        // when
        AuthorityProfile created = AuthorityProfile.createWithGroup(UUID.randomUUID(), givenPermissionGroup, 3);

        // then
        assertThat(created.getGroupAuthorities())
                .hasSize(1);

        List<GrantedGroupAuthority> grantedGroupAuthorities = created.getGroupAuthorities().stream()
                .filter(ga -> ga.getGroup().getId().equals(givenPermissionGroup.getId()))
                .toList();

        assertThat(grantedGroupAuthorities)
                .isNotEmpty();

        GrantedGroupAuthority ga = grantedGroupAuthorities.get(0);

        assertThat(ga.getAuthorityLevel())
                .isEqualTo(3);
    }

    @Test
    void createWithGroup_granted_group_authority_field_should_not_null() {
        // when
        AuthorityProfile authorityProfile = AuthorityProfile.createWithGroup(UUID.randomUUID(), givenPermissionGroup, 3);
        GrantedGroupAuthority grantedGroupAuthority = authorityProfile.getGroupAuthorities().get(0);

        // then
        assertThat(grantedGroupAuthority.getGroup())
                .isNotNull();
        assertThat(grantedGroupAuthority.getGroup().getId())
                .isNotNull();
        assertThat(grantedGroupAuthority.getProfile())
                .isNotNull();
        assertThat(grantedGroupAuthority.getProfile().getId())
                .isNotNull();
    }

    @Test
    void validateAuthority_always_success_with_0_level_requirement() {
        // given
        AuthorityProfile given = AuthorityProfile.createWithGroup(UUID.randomUUID(), givenPermissionGroup, 3);

        // when
        assertThatCode(() -> given.validateAuthority(givenPermissionGroup.getId(), 0))
                .doesNotThrowAnyException();
    }

    @Test
    void validateAuthority_should_throw_exception_if_authority_level_is_not_enough() {
        // given
        AuthorityProfile given = AuthorityProfile.createWithGroup(UUID.randomUUID(), givenPermissionGroup, 3);

        // when
        assertThatThrownBy(() -> given.validateAuthority(givenPermissionGroup.getId(), 4))
                .isInstanceOf(PermissionEvaluationException.class)
                .hasMessageContaining("not enough")
        ;

    }
}