package dev.prvt.yawiki.core.permission.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dev.prvt.yawiki.core.permission.domain.ActionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class PermissionTest {
    private Permission givenPermission;

    @BeforeEach
    void init() {
        givenPermission = Permission.builder()
                .create(0)
                .read(1)
                .update(2)
                .delete(3)
                .manage(4)
                .build();
    }

    @Test
    void getRequiredLevel() {
        assertThat(
                tuple(
                        givenPermission.getRequiredLevel(CREATE),
                        givenPermission.getRequiredLevel(READ),
                        givenPermission.getRequiredLevel(UPDATE),
                        givenPermission.getRequiredLevel(DELETE),
                        givenPermission.getRequiredLevel(MANAGE)))
                .isEqualTo(
                        tuple(0, 1, 2, 3, 4)
                );
    }
}