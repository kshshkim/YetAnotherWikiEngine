package dev.prvt.yawiki.core.permission.infra;

import dev.prvt.yawiki.core.permission.domain.model.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.model.GrantedPermission;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DataJpaTest
public class AuthorityProfileRepositoryCascadePersistTest {

    @Autowired
    EntityManager em;

    @Autowired
    AuthorityProfileRepository authorityProfileRepository;

    @Test
    @DisplayName("cascade 영속화 테스트")
    void testSave() {
        // given
        UUID givenId = UUID.randomUUID();
        PermissionLevel givenPermissionLevel = PermissionLevel.NEW_MEMBER;
        AuthorityProfile givenAuthorityProfile = AuthorityProfile.create(givenId, givenPermissionLevel);

        // when
        authorityProfileRepository.save(givenAuthorityProfile);
        em.flush();
        em.clear();

        // then
        Optional<AuthorityProfile> result = authorityProfileRepository.findById(givenId);

        assertThat(result).isPresent();
        AuthorityProfile actual = result.orElseThrow();

        assertThat(actual.getGrantedPermissions())
                .describedAs("cascade 통해 GrantedPermission 영속화 되었는지 체크")
                .hasSize(1);

        assertThat(actual.getMaxPermissionLevel(0))
                .describedAs("권한 레벨이 제대로 들어갔는지 체크")
                .isEqualTo(PermissionLevel.NEW_MEMBER);

        assertThat(actual.isNew())
                .describedAs("persistable 인터페이스 구현이 제대로 됨.")
                .isFalse();
    }

    @Test
    @DisplayName("따로 생성해서 create 메서드로 넘겼을 때 cascade 영속화 테스트")
    void testSave_custom_GrantedPermission() {
        UUID givenId = UUID.randomUUID();
        PermissionLevel givenPermissionLevel = PermissionLevel.MANAGER;
        String givenComment = randString();
        LocalDateTime givenExpiresAt = LocalDateTime.now().plusMinutes(10L);

        AuthorityProfile givenGranter = AuthorityProfile.create(UUID.randomUUID(), PermissionLevel.MANAGER);
        em.persist(givenGranter);
        em.flush();

        GrantedPermission givenGrantedPermission = GrantedPermission.create(
                givenGranter,
                givenPermissionLevel,
                givenComment,
                givenExpiresAt
        );

        AuthorityProfile givenAuthorityProfile = AuthorityProfile.create(givenId, givenGrantedPermission);

        // when
        authorityProfileRepository.save(givenAuthorityProfile);
        em.flush();
        em.clear();

        // then
        Optional<AuthorityProfile> result = authorityProfileRepository.findById(givenId);

        assertThat(result).isPresent();
        AuthorityProfile actual = result.orElseThrow();

        assertThat(actual.getGrantedPermissions())
                .describedAs("cascade 통해 GrantedPermission 영속화 되었는지 체크")
                .hasSize(1);

        assertThat(actual.getMaxPermissionLevel(0))
                .describedAs("권한 레벨이 제대로 들어갔는지 체크")
                .isEqualTo(givenPermissionLevel);

        assertThatCode(() -> actual.getGrantedPermissions().get(0))
                .describedAs("생성시 파라미터로 넘긴 GrantedPermission 이 존재해야함.")
                .doesNotThrowAnyException();

        GrantedPermission actualGrantedPermission = actual.getGrantedPermissions().get(0);

        assertThat(actualGrantedPermission.getComment())
                .describedAs("comment 제대로 설정되어야함.")
                .isEqualTo(givenComment);

        assertThat(actualGrantedPermission.getExpiresAt())
                .describedAs("만료일이 제대로 설정되어야함.")
                .isEqualTo(givenExpiresAt);

        AuthorityProfile granter = actualGrantedPermission.getGranter();

        assertThat(granter)
                .describedAs("권한 부여자가 null 이어선 안 됨.")
                .isNotNull();

        assertThat(granter.getId())
                .describedAs("권한 부여자 외래키가 제대로 설정됨.")
                .isEqualTo(givenGranter.getId());
    }
}
