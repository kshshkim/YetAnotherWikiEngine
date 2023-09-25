package dev.prvt.yawiki.core.permission.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class PermissionDataTest {

    @Test
    void test_from() {
        Permission given = Permission.builder()
                .create(0)
                .read(1)
                .update(2)
                .delete(3)
                .manage(4)
                .build();
        PermissionData from = PermissionData.from(given);
        assertThat(tuple(from.getC(), from.getR(), from.getU(), from.getD(), from.getM()))
                .isEqualTo(tuple(0, 1, 2, 3, 4));
    }
}