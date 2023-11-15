package dev.prvt.yawiki.core.permission.infra;

import dev.prvt.yawiki.core.permission.domain.AuthorityGrantValidator;
import dev.prvt.yawiki.core.permission.domain.model.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

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
    @Mock
    AuthorityGrantValidator mockAuthorityGrantValidator;

    static Stream<Arguments> permissionLevelsHigherThanEveryone() {
        return Arrays.stream(PermissionLevel.values())
                .filter(permissionLevel -> !permissionLevel.equals(PermissionLevel.EVERYONE))
                .map(Arguments::arguments);
    }

    @BeforeEach
    void init() {
        repositoryAuthorityLevelFinder = new RepositoryAuthorityLevelFinder(authorityProfileRepository);
        givenActorId = UUID.randomUUID();

    }

    @ParameterizedTest
    @MethodSource("permissionLevelsHigherThanEveryone")
    void find(PermissionLevel givenPermissionLevel) {
        // given
        AuthorityProfile authorityProfile = AuthorityProfile.create(givenActorId);

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