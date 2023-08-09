package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;
import dev.prvt.yawiki.core.wikipage.domain.exception.VersionCollisionException;
import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageReferenceUpdaterException;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.VersionCollisionValidator;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPagePermissionValidator;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.WikiReferenceUpdater;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.*;

class WikiPageDomainServiceTest {
    private final String UPDATE_REFERENCE_EXCEPTION_TRIGGER = randomUUID().toString();
    private final String UPDATE_REFERENCE_EXCEPTION_TRIGGERED_MESSAGE = randomUUID().toString();

    private final UUID UPDATE_PERMISSION_EXCEPTION_TRIGGER = randomUUID();
    private final String UPDATE_PERMISSION_MESSAGE = randomUUID().toString();

    private final UUID PROCLAIM_PERMISSION_EXCEPTION_TRIGGER = randomUUID();
    private final String PROCLAIM_PERMISSION_EXCEPTION_MESSAGE = randomUUID().toString();

    private final String COLLISION_FAIL_TRIGGER = randomUUID().toString();
    private final String COLLISION_VALIDATION_FAIL_MESSAGE = randomUUID().toString();


    private boolean wikiReferenceUpdaterCalled;
    private boolean updatePermissionValidatorCalled;
    private boolean updateProclaimPermissionValidatorCalled;
    private boolean versionCollisionValidatorCalled;

    private final WikiReferenceUpdater dummyWikiReferenceUpdater = new WikiReferenceUpdater() {
        @Override
        public void updateReferences(UUID documentId, Set<String> referencedTitles) {
            wikiReferenceUpdaterCalled = true;
            if (referencedTitles.contains(UPDATE_REFERENCE_EXCEPTION_TRIGGER)) {
                throw new RuntimeException(UPDATE_REFERENCE_EXCEPTION_TRIGGERED_MESSAGE);
            }
        }
    };

    private final WikiPagePermissionValidator dummyWikiPagePermissionValidator = new WikiPagePermissionValidator() {
        @Override
        public void validateUpdate(UUID actorId, WikiPage wikiPage) throws UpdatePermissionException {
            updatePermissionValidatorCalled = true;
            if (actorId.equals(UPDATE_PERMISSION_EXCEPTION_TRIGGER)) {
                throw new RuntimeException(UPDATE_PERMISSION_MESSAGE);
            }
        }

        @Override
        public void validateUpdateProclaim(UUID actorId, WikiPage wikiPage) {
            if (actorId.equals(PROCLAIM_PERMISSION_EXCEPTION_TRIGGER)) {
                throw new RuntimeException(PROCLAIM_PERMISSION_EXCEPTION_MESSAGE);
            }
            updateProclaimPermissionValidatorCalled = true;
        }
    };

    private final VersionCollisionValidator dummyVersionCollisionValidator = new VersionCollisionValidator() {
        @Override
        public void validate(WikiPage wikiPage, String versionToken) throws VersionCollisionException {
            versionCollisionValidatorCalled = true;
            if (versionToken.equals(COLLISION_FAIL_TRIGGER)) {
                throw new RuntimeException(COLLISION_VALIDATION_FAIL_MESSAGE);
            }
        }
    };


    private final WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();

    private final WikiPageDomainService wikiPageDomainService = new WikiPageDomainService(wikiPageRepository, dummyWikiReferenceUpdater, dummyVersionCollisionValidator, dummyWikiPagePermissionValidator);

    private WikiPage givenWikiPage;
    private UUID givenActorId;
    private String givenTitle;
    private String givenContent;
    private String givenComment;
    private String givenVersionToken;
    private String givenReference;
    private Set<String> givenReferences;


    @BeforeEach
    void init() {
        wikiReferenceUpdaterCalled = false;
        versionCollisionValidatorCalled = false;
        updatePermissionValidatorCalled = false;
        updateProclaimPermissionValidatorCalled = false;

        givenTitle = randomUUID().toString();
        givenWikiPage = wikiPageRepository.save(WikiPage.create(givenTitle));
        givenActorId = randomUUID();
        givenContent = randomUUID().toString();
        givenComment = randomUUID().toString();
        givenVersionToken = givenWikiPage.getVersionToken();
        givenReference = randomUUID().toString();
        givenReferences = Set.of(givenReference);
    }

    void commitUpdate() {
        wikiPageDomainService.commitUpdate(givenActorId, givenTitle, givenContent, givenComment, givenVersionToken, givenReferences);
    }

    void commitUpdate_with_titleThatDoesNotExists() {
        wikiPageDomainService.commitUpdate(givenActorId, randomUUID().toString(), givenContent, givenComment, givenVersionToken, givenReferences);
    }

    void commitUpdate_with_UPDATE_REFERENCE_EXCEPTION_TRIGGER() {
        wikiPageDomainService.commitUpdate(givenActorId, givenTitle, givenContent, givenComment, givenVersionToken, Set.of(UPDATE_REFERENCE_EXCEPTION_TRIGGER));
    }

    void commitUpdate_with_UPDATE_PERMISSION_EXCEPTION_TRIGGER() {
        wikiPageDomainService.commitUpdate(UPDATE_PERMISSION_EXCEPTION_TRIGGER, givenTitle, givenContent, givenComment, givenVersionToken, givenReferences);
    }

    @Test
    void commitUpdate_should_fail_when_wiki_page_does_not_exist() {
        assertThatThrownBy(this::commitUpdate_with_titleThatDoesNotExists)
                .isInstanceOf(NoSuchWikiPageException.class)
        ;
    }
    @Test
    void commitUpdate_should_throw_WikiPageReferenceUpdaterException_when_update_failed() {
        assertThatThrownBy(this::commitUpdate_with_UPDATE_REFERENCE_EXCEPTION_TRIGGER)
                .isInstanceOf(WikiPageReferenceUpdaterException.class)
                .hasMessageContaining(UPDATE_REFERENCE_EXCEPTION_TRIGGERED_MESSAGE)
        ;
    }

    @Test
    void commitUpdate_should_fail_update_if_update_permission_validator_fails() {
        assertThatThrownBy(this::commitUpdate_with_UPDATE_PERMISSION_EXCEPTION_TRIGGER)
                .hasMessageContaining(UPDATE_PERMISSION_MESSAGE)
        ;
    }

    @Test
    void commitUpdate_should_success_and_call_wikiReferenceUpdater_and_proper_wikiPagePermissionValidator_methods() {
        // when
        assertThatCode(this::commitUpdate)
                .doesNotThrowAnyException();

        // then
        assertThat(versionCollisionValidatorCalled)
                .isTrue();
        assertThat(updatePermissionValidatorCalled)
                .isTrue();
        assertThat(wikiReferenceUpdaterCalled)
                .isTrue();
    }

    @Test
    void commitUpdate_should_success_and_update_with_proper_parameters() {
        // when
        assertThatCode(this::commitUpdate)
                .doesNotThrowAnyException();

        // then
        WikiPage found = wikiPageRepository.findByTitleWithRevisionAndRawContent(givenTitle).orElseThrow();

        assertThat(tuple(found.getId(), found.getTitle(), found.getContent()))
                .isEqualTo(tuple(givenWikiPage.getId(), givenTitle, givenContent));
        Revision foundCurrentRevision = found.getCurrentRevision();
        assertThat(foundCurrentRevision)
                .isNotNull();
        assertThat(foundCurrentRevision.getContributorId())
                .isEqualTo(givenActorId);
    }

    @Test
    void proclaimUpdate_should_call_validator_when_proclaim() {
        wikiPageDomainService.proclaimUpdate(givenActorId, givenTitle);
        assertThat(updateProclaimPermissionValidatorCalled).isTrue();
    }

    @Test
    void proclaimUpdate_should_fail_if_permission_validating_fails() {
        assertThatThrownBy(() -> wikiPageDomainService.proclaimUpdate(PROCLAIM_PERMISSION_EXCEPTION_TRIGGER, givenTitle))
                .hasMessage(PROCLAIM_PERMISSION_EXCEPTION_MESSAGE);
    }

    @Test
    void proclaimUpdate_should_create_when_does_not_exist() {
        String notExists = randomUUID().toString();

        WikiPage wikiPage = wikiPageDomainService.proclaimUpdate(givenActorId, notExists);

        assertThat(wikiPage)
                .describedAs("WikiPage 가 생성되어야함.")
                .isNotNull();

        assertThat(wikiPageRepository.findByTitle(notExists))
                .describedAs("생성된 WikiPage 가 영속화 되어야함.")
                .isPresent();

        assertThat(wikiPage.getTitle())
                .describedAs("생성된 WikiPage 의 문서 제목이 동일함.")
                .isEqualTo(notExists);

        assertThat(wikiPage.isActive())
                .describedAs("새로 생성된 WikiPage 엔티티의 isActive 는 false 임")
                .isFalse();
    }

    @Test
    void proclaimUpdate_should_get_when_exists() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));
        saved.update(randomUUID(), randString(), randString());
        String savedContent = saved.getContent();

        // when
        WikiPage when = wikiPageDomainService.proclaimUpdate(randomUUID(), saved.getTitle());

        // then
        assertThat(when.getContent())
                .isEqualTo(savedContent);

    }
}