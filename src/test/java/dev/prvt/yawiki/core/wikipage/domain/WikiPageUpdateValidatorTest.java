package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;
import dev.prvt.yawiki.core.wikipage.domain.exception.VersionCollisionException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.UpdatePermissionValidator;
import dev.prvt.yawiki.core.wikipage.domain.validator.VersionCollisionValidator;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.*;

class WikiPageUpdateValidatorTest {
    private final UUID PERMISSION_FAIL_TRIGGER = UUID.randomUUID();
    private final String PERMISSION_VALIDATION_FAIL_MESSAGE = randString();
    private final String COLLISION_FAIL_TRIGGER = UUID.randomUUID().toString();
    private final String COLLISION_VALIDATION_FAIL_MESSAGE = randString();

    private final WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();

    private boolean updatePermissionValidatorCalled;
    private boolean versionCollisionValidatorCalled;

    private String givenTitle;
    private UUID givenActorId;
    private UUID givenWikiPageId;
    private String givenVersionToken;

    private final UpdatePermissionValidator dummyUpdatePermissionValidator = new UpdatePermissionValidator() {
        @Override
        public void validateUpdate(UUID wikiPageId, UUID actorId) throws UpdatePermissionException {
            updatePermissionValidatorCalled = true;
            if (actorId.equals(PERMISSION_FAIL_TRIGGER)) {
                throw new RuntimeException(PERMISSION_VALIDATION_FAIL_MESSAGE);
            }
            assertThat(wikiPageId)
                    .describedAs("파라미터가 올바르게 넘어왔는지 확인")
                    .isEqualTo(givenWikiPageId);
            assertThat(actorId)
                    .describedAs("파라미터가 올바르게 넘어왔는지 확인")
                    .isEqualTo(givenActorId);
        }
    };

    private final VersionCollisionValidator dummyVersionCollisionValidator = new VersionCollisionValidator() {
        @Override
        public void validate(WikiPage wikiPage, String versionToken) throws VersionCollisionException {
            versionCollisionValidatorCalled = true;
            if (versionToken.equals(COLLISION_FAIL_TRIGGER)) {
                throw new RuntimeException(COLLISION_VALIDATION_FAIL_MESSAGE);
            }
            assertThat(wikiPage.getId())
                    .describedAs("파라미터가 올바르게 넘어왔는지 확인")
                    .isEqualTo(givenWikiPageId);
            assertThat(versionToken)
                    .describedAs("파라미터가 올바르게 넘어왔는지 확인")
                    .isEqualTo(givenVersionToken);
        }
    };

    private final WikiPageUpdateValidator wikiPageUpdateValidator = new WikiPageUpdateValidator(wikiPageRepository, dummyUpdatePermissionValidator, dummyVersionCollisionValidator);

    @BeforeEach
    void init() {
        updatePermissionValidatorCalled = false;
        versionCollisionValidatorCalled = false;
        givenTitle = UUID.randomUUID().toString();

        givenActorId = UUID.randomUUID();
        givenVersionToken = UUID.randomUUID().toString();

        WikiPage saved = wikiPageRepository.save(WikiPage.create(givenTitle));
        givenWikiPageId = saved.getId();
    }

    @Test
    void should_fail_if_permission_validate_fail() {
        assertThatThrownBy(() -> wikiPageUpdateValidator.validate(PERMISSION_FAIL_TRIGGER, givenTitle, givenVersionToken))
                .hasMessageContaining(PERMISSION_VALIDATION_FAIL_MESSAGE);
    }

    @Test
    void should_fail_if_collision_validate_fail() {
        assertThatThrownBy(() -> wikiPageUpdateValidator.validate(givenActorId, givenTitle, COLLISION_FAIL_TRIGGER))
                .hasMessageContaining(COLLISION_VALIDATION_FAIL_MESSAGE);
    }

    @Test
    void should_fail_if_no_wiki_page_found() {
        assertThatThrownBy(() -> wikiPageUpdateValidator.validate(givenActorId, "title that doesn't exist " + randString(), givenVersionToken))
                .isInstanceOf(NoSuchWikiPageException.class);
    }

    @Test
    void should_success_and_should_call_all_validators() {
        assertThatCode(() -> wikiPageUpdateValidator.validate(givenActorId, givenTitle, givenVersionToken))
                .doesNotThrowAnyException();
        assertThat(updatePermissionValidatorCalled).isTrue();
        assertThat(versionCollisionValidatorCalled).isTrue();
    }

}