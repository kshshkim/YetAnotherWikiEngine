package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.fixture.PermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.prvt.yawiki.core.permission.domain.PermissionLevel.*;
import static org.assertj.core.api.Assertions.*;

class NamespacePermissionTest {
    Permission.PermissionBuilder givenPermissionBuilder;
    NamespacePermission.NamespacePermissionBuilder givenNamespacePermissionBuilder;

    @BeforeEach
    void init() {
        givenPermissionBuilder = PermissionFixture.aPermission()
                .create(ADMIN)
                .delete(ASSISTANT_MANAGER)
                .rename(MEMBER)
                .editRequest(MEMBER)
                .editCommit(ASSISTANT_MANAGER)
                .description("hi")
                ;

        givenNamespacePermissionBuilder = NamespacePermission.builder()
                .upwardOverridable(true)
                .downwardOverridable(true)
                ;
    }

    @Test
    void getRequiredPermissionLevel() {
        Permission givenPermission = givenPermissionBuilder.build();
        NamespacePermission givenNamespacePermission = givenNamespacePermissionBuilder
                .permission(givenPermission)
                .build();
        for (ActionType actionType : ActionType.values()) {
            assertThat(givenNamespacePermission.getRequiredPermissionLevel(actionType))
                    .isEqualTo(givenPermission.getPermissionLevel(actionType));
        }
    }

    @Test
    @DisplayName("네임스페이스 권한은 null이면 안 됨.")
    void validateNamespacePermission_on_creation() {
        List<Permission> givenPermissions = List.of(
                PermissionFixture.aPermission().create(null).build(),
                PermissionFixture.aPermission().rename(null).build(),
                PermissionFixture.aPermission().delete(null).build(),
                PermissionFixture.aPermission().editCommit(null).build(),
                PermissionFixture.aPermission().editRequest(null).build(),
                PermissionFixture.aPermission().discussionCreate(null).build(),
                PermissionFixture.aPermission().discussionParticipate(null).build()
        );

        for (Permission givenPermission : givenPermissions) {
            assertThatThrownBy(() -> givenNamespacePermissionBuilder.permission(givenPermission).build())
                    .hasMessageContaining("namespace")
                    .hasMessageContaining("cannot contain null");
        }
    }

    @Test
    void validatePermissionOverride_downward_restrict() {
        List<Permission> givenPermissions = List.of(
                PermissionFixture.aPermission().create(ADMIN).build(),
                PermissionFixture.aPermission().rename(ADMIN).build(),
                PermissionFixture.aPermission().delete(ADMIN).build(),
                PermissionFixture.aPermission().editCommit(ADMIN).build(),
                PermissionFixture.aPermission().editRequest(ADMIN).build(),
                PermissionFixture.aPermission().discussionCreate(ADMIN).build(),
                PermissionFixture.aPermission().discussionParticipate(ADMIN).build()
        );


        for (Permission givenPermission : givenPermissions) {
            NamespacePermission givenNamespacePermission = givenNamespacePermissionBuilder
                    .downwardOverridable(false)
                    .upwardOverridable(true)
                    .permission(givenPermission)
                    .build();
            assertThatThrownBy(() -> givenNamespacePermission.validatePermissionOverride(PermissionFixture.aPermission().build()))
                    .hasMessageContaining("downward override forbidden")
                    .hasMessageContaining(ADMIN.toString())
                    .hasMessageContaining(EVERYONE.toString())
            ;
        }
    }

    @Test
    void validatePermissionOverride_upward_restrict() {
        List<Permission> givenPermissions = List.of(
                PermissionFixture.aPermission().create(ADMIN).build(),
                PermissionFixture.aPermission().rename(ADMIN).build(),
                PermissionFixture.aPermission().delete(ADMIN).build(),
                PermissionFixture.aPermission().editCommit(ADMIN).build(),
                PermissionFixture.aPermission().editRequest(ADMIN).build(),
                PermissionFixture.aPermission().discussionCreate(ADMIN).build(),
                PermissionFixture.aPermission().discussionParticipate(ADMIN).build()
        );

        NamespacePermission givenNamespacePermission = givenNamespacePermissionBuilder
                .downwardOverridable(true)
                .upwardOverridable(false)
                .permission(PermissionFixture.aPermission().build())
                .build();

        for (Permission givenPermission : givenPermissions) {
            assertThatThrownBy(() -> givenNamespacePermission.validatePermissionOverride(givenPermission))
                    .hasMessageContaining("upward override forbidden")
                    .hasMessageContaining(ADMIN.toString())
                    .hasMessageContaining(EVERYONE.toString())
            ;
        }
    }

    @Test
    void validatePermissionOverride_not_allowed() {
        List<Permission> givenPermissions = List.of(
                PermissionFixture.aPermission().create(ADMIN).build(),
                PermissionFixture.aPermission().rename(ADMIN).build(),
                PermissionFixture.aPermission().delete(ADMIN).build(),
                PermissionFixture.aPermission().editCommit(ADMIN).build(),
                PermissionFixture.aPermission().editRequest(ADMIN).build(),
                PermissionFixture.aPermission().discussionCreate(ADMIN).build(),
                PermissionFixture.aPermission().discussionParticipate(ADMIN).build()
        );

        NamespacePermission givenNamespacePermission = givenNamespacePermissionBuilder
                .downwardOverridable(false)
                .upwardOverridable(false)
                .permission(PermissionFixture.aPermission().build())
                .build();

        for (Permission givenPermission : givenPermissions) {
            assertThatThrownBy(() -> givenNamespacePermission.validatePermissionOverride(givenPermission))
                    .hasMessageContaining("override forbidden")
                    .hasMessageContaining("namespaceId:")
            ;
        }
    }
}