package dev.prvt.yawiki.core.permission.domain.model;

import dev.prvt.yawiki.fixture.PermissionFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

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
        // given
        LocalDateTime givenExp = LocalDateTime.now().plusMinutes(10L);
        String givenComment = randString();
        PermissionLevel givenPermissionLevel = PermissionLevel.MEMBER;
        AuthorityProfile givenGranter = AuthorityProfile.create(UUID.randomUUID(), PermissionLevel.MANAGER);

        // when
        GrantedPermission result = GrantedPermission.create(givenGranter, givenPermissionLevel, givenComment, givenExp);

        // then
        assertThat(List.of(result.getExpiresAt(), result.getComment(), result.getPermissionLevel()))
                .describedAs("넘긴 파라미터대로 잘 생성되었는지 검증")
                .containsExactly(givenExp, givenComment, givenPermissionLevel);

        assertThat(result.getGranter())
                .describedAs("granter 가 잘 설정됐는지 확인")
                .isSameAs(givenGranter);
    }
}