package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;
import dev.prvt.yawiki.core.wikipage.domain.exception.VersionCollisionException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.*;

class WikiPageValidatorTest {
    private final UUID UPDATE_PERMISSION_FAIL_TRIGGER = UUID.randomUUID();
    private final String UPDATE_PERMISSION_VALIDATION_FAIL_MESSAGE = randString();
    private final UUID PROCLAIM_PERMISSION_FAIL_TRIGGER = UUID.randomUUID();
    private final String PROCLAIM_PERMISSION_VALIDATION_FAIL_MESSAGE = randString();
    private final String COLLISION_FAIL_TRIGGER = UUID.randomUUID().toString();
    private final String COLLISION_VALIDATION_FAIL_MESSAGE = randString();

    private final WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();

    private boolean updatePermissionValidatorCalled;
    private boolean updateProclaimPermissionValidatorCalled;
    private boolean versionCollisionValidatorCalled;

    private UUID givenActorId;
    private UUID givenWikiPageId;
    private WikiPage givenWikiPage;
    private String givenVersionToken;

    private final UpdatePermissionValidator dummyUpdatePermissionValidator = new UpdatePermissionValidator() {
        @Override
        public void validateUpdate(UUID actorId, UUID wikiPageId) throws UpdatePermissionException {
            updatePermissionValidatorCalled = true;
            if (actorId.equals(UPDATE_PERMISSION_FAIL_TRIGGER)) {
                throw new RuntimeException(UPDATE_PERMISSION_VALIDATION_FAIL_MESSAGE);
            }

            assertThat(tuple(wikiPageId, actorId))
                    .describedAs("UpdatePermissionValidator 파라미터가 올바르게 넘어왔는지 확인")
                    .isEqualTo(tuple(givenWikiPageId, givenActorId));
        }

        @Override
        public void validateUpdateProclaim(UUID actorId, UUID wikiPageId) {
            if (actorId.equals(PROCLAIM_PERMISSION_FAIL_TRIGGER)) {
                throw new RuntimeException(PROCLAIM_PERMISSION_VALIDATION_FAIL_MESSAGE);
            }
            updateProclaimPermissionValidatorCalled = true;
            assertThat(tuple(actorId, wikiPageId))
                    .isEqualTo(tuple(givenActorId, givenWikiPageId));
        }
    };

    private final VersionCollisionValidator dummyVersionCollisionValidator = new VersionCollisionValidator() {
        @Override
        public void validate(WikiPage wikiPage, String versionToken) throws VersionCollisionException {
            versionCollisionValidatorCalled = true;
            if (versionToken.equals(COLLISION_FAIL_TRIGGER)) {
                throw new RuntimeException(COLLISION_VALIDATION_FAIL_MESSAGE);
            }

            assertThat(tuple(wikiPage.getId(), versionToken))
                    .describedAs("VersionCollisionValidator 파라미터가 올바르게 넘어왔는지 확인")
                    .isEqualTo(tuple(givenWikiPageId, givenVersionToken));
        }
    };

    private final WikiPageValidator wikiPageValidator = new WikiPageValidator(dummyUpdatePermissionValidator, dummyVersionCollisionValidator);

    @BeforeEach
    void init() {
        updatePermissionValidatorCalled = false;
        versionCollisionValidatorCalled = false;

        givenActorId = UUID.randomUUID();
        givenVersionToken = UUID.randomUUID().toString();

        givenWikiPage = wikiPageRepository.save(WikiPage.create(randString()));
        givenWikiPageId = givenWikiPage.getId();
    }

    @Test
    void should_fail_if_permission_validate_fail() {
        assertThatThrownBy(() -> wikiPageValidator.validateUpdateCommit(UPDATE_PERMISSION_FAIL_TRIGGER, givenVersionToken, givenWikiPage))
                .hasMessageContaining(UPDATE_PERMISSION_VALIDATION_FAIL_MESSAGE);
    }

    @Test
    void should_fail_if_collision_validate_fail() {
        assertThatThrownBy(() -> wikiPageValidator.validateUpdateCommit(givenActorId, COLLISION_FAIL_TRIGGER, givenWikiPage))
                .hasMessageContaining(COLLISION_VALIDATION_FAIL_MESSAGE);
    }

    @Test
    void should_success_and_should_call_all_validators() {
        assertThatCode(() -> wikiPageValidator.validateUpdateCommit(givenActorId, givenVersionToken, givenWikiPage))
                .doesNotThrowAnyException();
        assertThat(updatePermissionValidatorCalled).isTrue();
        assertThat(versionCollisionValidatorCalled).isTrue();
    }

    @Test
    void should_success_and_should_call_proclaim_validator() {
        assertThatCode(() -> wikiPageValidator.validateUpdateProclaim(givenActorId, givenWikiPage))
                .doesNotThrowAnyException();
        assertThat(updateProclaimPermissionValidatorCalled).isTrue();
    }

    @Test
    void should_fail_if_permission_validator_fail() {
        assertThatThrownBy(() -> wikiPageValidator.validateUpdateProclaim(PROCLAIM_PERMISSION_FAIL_TRIGGER, givenWikiPage))
                .hasMessage(PROCLAIM_PERMISSION_VALIDATION_FAIL_MESSAGE);
    }

}