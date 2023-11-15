package dev.prvt.yawiki.core.permission.domain.model;

import dev.prvt.yawiki.fixture.PermissionFixture;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static dev.prvt.yawiki.core.permission.domain.model.ActionType.*;
import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PermissionTest {
    static Random random = new Random();
    static PermissionLevel[] permissionLevels = PermissionLevel.values();


    PermissionLevel randomPermissionLevel() {
        return permissionLevels[random.nextInt(0, permissionLevels.length)];
    }

    /**
     * 무작위 레벨 설정, 모든 ActionType 에 대해서 바른 값을 찾는지 확인
     */
    @RepeatedTest(value = 10)
    void getPermissionLevel() {
        Map<ActionType, PermissionLevel> givenPermissions = new HashMap<>();
        for (ActionType value : values()) {
            givenPermissions.put(value, randomPermissionLevel());
        }

        Permission givenPermission = Permission.builder()
                .create(givenPermissions.get(CREATE))
                .editRequest(givenPermissions.get(EDIT_REQUEST))
                .editCommit(givenPermissions.get(EDIT_COMMIT))
                .rename(givenPermissions.get(RENAME))
                .delete(givenPermissions.get(DELETE))
                .discussionParticipate(givenPermissions.get(DISCUSSION_PARTICIPATE))
                .discussionCreate(givenPermissions.get(DISCUSSION_CREATE))
                .description(randString())
                .build();

        for (ActionType actionType : values()) {
            assertThat(givenPermission.getPermissionLevel(actionType))
                    .describedAs("getPermissionLevel(" + actionType + ") failed.")
                    .isEqualTo(givenPermissions.get(actionType));
        }
    }

    @Test
    void creation_description_cannot_be_null() {
        assertThatThrownBy(() -> PermissionFixture.aPermission()
                .description(null)
                .build())
                .hasMessageContaining("description")
                .describedAs("cannot be null")
        ;
    }
}