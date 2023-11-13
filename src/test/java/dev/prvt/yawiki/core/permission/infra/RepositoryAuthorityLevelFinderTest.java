package dev.prvt.yawiki.core.permission.infra;

import dev.prvt.yawiki.core.permission.domain.AuthorityGrantValidator;
import dev.prvt.yawiki.core.permission.domain.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.PermissionLevel;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class RepositoryAuthorityLevelFinderTest {

    @Autowired
    EntityManager em;
    @Autowired
    AuthorityProfileRepository authorityProfileRepository;

    RepositoryAuthorityLevelFinder repositoryAuthorityLevelFinder;

    UUID givenActorId;

    @BeforeEach
    void init() {
        repositoryAuthorityLevelFinder = new RepositoryAuthorityLevelFinder(authorityProfileRepository);
        givenActorId = UUID.randomUUID();

    }

    @Mock
    AuthorityGrantValidator mockAuthorityGrantValidator;

    @ParameterizedTest
    @ValueSource(strings = {"MEMBER", "MANAGER", "ADMIN"})
    void find(String authority) {
        // given
        PermissionLevel givenPermissionLevel = PermissionLevel.valueOf(authority);

        AuthorityProfile authorityProfile = AuthorityProfile
                .builder()
                .id(givenActorId)
                .contributorId(givenActorId)
                .build();

        authorityProfile.grantPermissionTo(authorityProfile, givenPermissionLevel, randString(), null, mockAuthorityGrantValidator);

        authorityProfileRepository.save(authorityProfile);
        em.flush();
        em.clear();

        // when
        PermissionLevel found = repositoryAuthorityLevelFinder.findPermissionLevelByActorId(givenActorId);

        // then
        assertThat(found)
                .isEqualTo(givenPermissionLevel);
    }

}