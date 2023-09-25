package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.PermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static dev.prvt.yawiki.core.permission.PermissionFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class ResourcePermissionTest {
    Permission givenPermission;
    PermissionGroup givenPermissionGroup;
    ResourcePermission givenResourcePermission;

    @BeforeEach
    void init() {
        givenPermission = aPermission().build();
        givenPermissionGroup = new PermissionGroup(UUID.randomUUID(), randString(), givenPermission);
        givenResourcePermission = new ResourcePermission(UUID.randomUUID(), givenPermissionGroup, null);
    }

    @Test
    void getRequiredPermission_when_specificPermission_is_null_should_return_owner_groups_default_permission() {
        // given
        givenResourcePermission = new ResourcePermission(UUID.randomUUID(), givenPermissionGroup, null);

        // when
        Permission actualPermission = givenResourcePermission.getRequiredPermission();

        // then
        assertThat(actualPermission)
                .isNotNull();
        assertThat(actualPermission.getId())
                .isEqualTo(givenResourcePermission.getOwnerGroup().getDefaultResourcePermission().getId());
    }

    @Test
    void getRequiredPermission_when_specificPermission_is_not_null_should_return_specificPermission() {
        // given
        givenResourcePermission = new ResourcePermission(UUID.randomUUID(), givenPermissionGroup, aPermission().build());

        // when
        Permission actualPermission = givenResourcePermission.getRequiredPermission();

        // then
        assertThat(actualPermission)
                .isNotNull();
        assertThat(actualPermission.getId())
                .isNotNull()
                .isNotEqualTo(givenPermission.getId())
                .isEqualTo(givenResourcePermission.getSpecificPermission().getId());
    }

    @Test
    void updatePermission_when_specificPermission_is_null() {
        // given
        Permission newPermission = aPermission().build();
        // when
        givenResourcePermission.updatePermission(newPermission);

        // then
        assertThat(givenResourcePermission.getSpecificPermission())
                .isNotNull();
        assertThat(givenResourcePermission.getSpecificPermission().getId())
                .isNotNull()
                .isEqualTo(newPermission.getId());
    }

    @Test
    void updatePermission_when_specificPermission_is_not_null() {
        // given
        Permission newPermission = aPermission().build();
        givenResourcePermission = new ResourcePermission(UUID.randomUUID(), givenPermissionGroup, givenPermission);

        // when
        givenResourcePermission.updatePermission(newPermission);

        // then
        assertThat(givenResourcePermission.getSpecificPermission())
                .isNotNull();
        assertThat(givenResourcePermission.getSpecificPermission().getId())
                .isNotNull()
                .isEqualTo(newPermission.getId());
    }

}