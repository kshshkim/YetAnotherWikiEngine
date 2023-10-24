package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.fixture.PermissionFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class GrantedPermissionTest {

    /**
     * 3분 후에 만료되는 권한에 대해서, 현재 시점에서의 유효성 여부와, 4분 후의 유효성 여부 검증
     */
    @Test
    void isValid_expiration_test() {
        GrantedPermission given = PermissionFixture.aGrantedPermission()
                .expiresAt(LocalDateTime.now().plusMinutes(3))  // 3분 후
                .build();
        assertThat(given.isValid(0))
                .describedAs("현재 시점에서 유효함.")
                .isTrue();
        assertThat(given.isValid(4))
                .describedAs("4분 후 시점에서 유효하지 않음.")
                .isFalse();
    }

    @Test
    void create() {
    }
}