package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.Fixture;
import dev.prvt.yawiki.core.permission.PermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
}