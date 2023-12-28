package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.model.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.model.GrantedPermission;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * AuthorityProfileCommandService 통합테스트
 */
@Transactional
@SpringBootTest
class AuthorityProfileCommandServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    AuthorityProfileCommandService authorityProfileCommandService;

    @Test
    void createAuthorityProfile() {
        // given
        UUID givenId = UUID.randomUUID();
        AuthorityProfile expected = AuthorityProfile.create(givenId);

        // when
        authorityProfileCommandService.createAuthorityProfile(givenId);
        em.flush();
        em.clear();

        // then
        AuthorityProfile actual = em.find(AuthorityProfile.class, givenId);
        assertThat(actual)
                .describedAs("정상적으로 영속화됨.")
                .isNotNull();

        assertThat(actual.getMaxPermissionLevel(0))
                .isEqualTo(expected.getMaxPermissionLevel(0));

        assertThat(actual.isNew())
                .isFalse();
    }

    @Test
    void grantAuthority() {
        // given
        UUID givenGranterId = UUID.randomUUID();
        AuthorityProfile granter = AuthorityProfile.create(givenGranterId, PermissionLevel.MANAGER);
        em.persist(granter);
        em.flush();
        em.clear();

        UUID givenGranteeId = UUID.randomUUID();
        authorityProfileCommandService.createAuthorityProfile(givenGranteeId);
        em.flush();
        em.clear();

        PermissionLevel givenGrantedAuthority = PermissionLevel.ASSISTANT_MANAGER;
        AuthorityGrantData givenData = new AuthorityGrantData(
                givenGranterId,
                givenGranteeId,
                givenGrantedAuthority,
                LocalDateTime.now().plusMinutes(10),
                randString()
        );

        // when
        authorityProfileCommandService.grantAuthority(givenData);
        em.flush();
        em.clear();

        // then
        AuthorityProfile actual = em.find(AuthorityProfile.class, givenGranteeId);
        assertThat(actual)
                .describedAs("정상적으로 영속화됨.")
                .isNotNull();

        Optional<GrantedPermission> grantedPermission = actual.getGrantedPermissions().stream()
                .filter(gp -> gp.getGranter() != null && gp.getGranter().getId().equals(givenGranterId))
                .findAny();

        assertThat(grantedPermission)
                .describedAs("granter id 가 일치하는 권한이 존재함.")
                .isPresent();

        GrantedPermission actualGrantedPermission = grantedPermission.orElseThrow();
        assertThat(List.of(actualGrantedPermission.getComment(), actualGrantedPermission.getExpiresAt()))
                .describedAs("comment, expiresAt 값이 제대로 설정됨.")
                .containsExactly(givenData.comment(), givenData.expiresAt());
    }
}