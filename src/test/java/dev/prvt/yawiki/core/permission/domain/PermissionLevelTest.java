package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dev.prvt.yawiki.core.permission.domain.model.PermissionLevel.values;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionLevelTest {
    @Test
    @DisplayName("권한 레벨 비교시, intValue를 기준으로 비교해야함.")
    void isHigherThanOrEqualTo() {
        PermissionLevel[] permissionLevels = values();
//        assertTrue(ADMIN.isHigherThanOrEqualTo(MANAGER));
//        assertTrue(ADMIN.isHigherThanOrEqualTo(ASSISTANT_MANAGER));
//        assertTrue(ADMIN.isHigherThanOrEqualTo(MEMBER));
//        ...
        for (PermissionLevel permissionLevelA : permissionLevels) {
            for (PermissionLevel permissionLevelB : permissionLevels) {
                assertEquals(
                        permissionLevelA.getIntValue() >= permissionLevelB.getIntValue(),
                        permissionLevelA.isHigherThanOrEqualTo(permissionLevelB));
            }
        }

    }
}