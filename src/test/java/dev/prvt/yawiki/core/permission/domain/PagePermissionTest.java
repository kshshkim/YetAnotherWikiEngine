package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.fixture.PermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PagePermissionTest {
    @BeforeEach
    void init() {
    }

    PagePermission givenPagePermission;
    Permission givenPageSpecificPermission;
    NamespacePermission givenNamespacePermission;


    @Test
    @DisplayName("PagePermission.permission이 null인 경우, PagePermission.namespacePermission의 값을 불러옴.")
    void getRequiredPermissionLevel_page_specific_permission_is_null() {
        PermissionLevel givenNamespacePermissionLevel = PermissionLevel.MANAGER;
        ActionType givenActionType = ActionType.EDIT_COMMIT;

        Permission permissionForNamespacePermission = PermissionFixture.aPermission()
                .description("namespace permission")
                .editCommit(givenNamespacePermissionLevel)
                .build();


        givenNamespacePermission = PermissionFixture.aNamespacePermission()
                .permission(permissionForNamespacePermission)
                .build();

        givenPagePermission = PagePermission.builder()
                .namespacePermission(givenNamespacePermission)
                .permission(null)
                .build();

        // when
        PermissionLevel result = givenPagePermission.getRequiredPermissionLevel(givenActionType);

        // then
        assertThat(result)
                .isEqualTo(givenNamespacePermissionLevel);

    }
    @Test
    @DisplayName("PagePermission.permission이 null이 아니지만, 해당되는 ActionType에 대한 값이 null인 경우, PagePermission.namespacePermission의 값을 불러옴.")
    void getRequiredPermissionLevel_page_specific_permission_is_present_but_permission_level_is_null() {
        PermissionLevel givenNamespacePermissionLevel = PermissionLevel.MANAGER;
        ActionType givenActionType = ActionType.EDIT_COMMIT;

        Permission permissionForNamespacePermission = PermissionFixture.aPermission()
                .description("namespace permission")
                .editCommit(givenNamespacePermissionLevel)
                .build();


        givenNamespacePermission = PermissionFixture.aNamespacePermission()
                .permission(permissionForNamespacePermission)
                .build();

        givenPageSpecificPermission = PermissionFixture.aPermission()
                .description("page permission")
                .editCommit(null)
                .build();

        givenPagePermission = PagePermission.builder()
                .namespacePermission(givenNamespacePermission)
                .permission(givenPageSpecificPermission)
                .build();

        // when
        PermissionLevel result = givenPagePermission.getRequiredPermissionLevel(givenActionType);

        // then
        assertThat(result)
                .isEqualTo(givenNamespacePermissionLevel);
    }
    @Test
    @DisplayName("PagePermission.permission이 null이 아니고, 해당되는 ActionType에 대한 PermissionLevel이 null이 아닌 경우, PagePermission의 값을 따름.")
    void getRequiredPermissionLevel_page_specific_permission_is_present_and_permission_level_is_not_null() {
        PermissionLevel givenNamespacePermissionLevel = PermissionLevel.MANAGER;
        PermissionLevel givenPageSpecificPermissionLevel = PermissionLevel.MEMBER;

        ActionType givenActionType = ActionType.EDIT_COMMIT;

        Permission permissionForNamespacePermission = PermissionFixture.aPermission()
                .description("namespace permission")
                .editCommit(givenNamespacePermissionLevel)
                .build();


        givenNamespacePermission = PermissionFixture.aNamespacePermission()
                .permission(permissionForNamespacePermission)
                .build();

        givenPageSpecificPermission = PermissionFixture.aPermission()
                .description("page permission")
                .editCommit(givenPageSpecificPermissionLevel)
                .build();

        givenPagePermission = PagePermission.builder()
                .namespacePermission(givenNamespacePermission)
                .permission(givenPageSpecificPermission)
                .build();

        // when
        PermissionLevel result = givenPagePermission.getRequiredPermissionLevel(givenActionType);

        // then
        assertThat(result)
                .isEqualTo(givenPageSpecificPermissionLevel);

    }

    @Mock
    PagePermissionUpdateValidator mockValidator;

    @Test
    void updatePermission() {
        // given
        givenPagePermission = PagePermission.builder()
                .permission(PermissionFixture.aPermission().build())
                .build();

        Permission newPermission = PermissionFixture.aPermission()
                .editCommit(null)
                .build();

        // when
        givenPagePermission.updatePermission(newPermission, mockValidator);

        // then
        verify(mockValidator).validate(givenPagePermission, newPermission);  // validate 가 적절한 인자와 함께 호출돼야함.
        assertThat(givenPagePermission.getPermission())
                .describedAs("mockValidator는 예외를 발생시키지 않으므로, 정상적으로 수행되어 permission 값이 업데이트 되어야함.")
                .isSameAs(newPermission);
    }
}