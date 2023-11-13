package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageCreatedEvent;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;
import dev.prvt.yawiki.core.wikipage.domain.exception.VersionCollisionException;
import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageReferenceUpdaterException;
import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.VersionCollisionValidator;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageCommandPermissionValidator;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.WikiReferenceUpdater;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.*;

class WikiPageDomainServiceTest {
    private final WikiPageTitle UPDATE_REFERENCE_EXCEPTION_TRIGGER = new WikiPageTitle(randomUUID().toString(), Namespace.NORMAL);
    private final String UPDATE_REFERENCE_EXCEPTION_TRIGGERED_MESSAGE = randomUUID().toString();

    private final UUID UPDATE_PERMISSION_EXCEPTION_TRIGGER = randomUUID();
    private final String UPDATE_PERMISSION_MESSAGE = randomUUID().toString();

    private final UUID DELETE_PERMISSION_EXCEPTION_TRIGGER = randomUUID();
    private final String DELETE_PERMISSION_MESSAGE = randomUUID().toString();

    private final UUID PROCLAIM_PERMISSION_EXCEPTION_TRIGGER = randomUUID();
    private final String PROCLAIM_PERMISSION_EXCEPTION_MESSAGE = randomUUID().toString();

    private final String COLLISION_FAIL_TRIGGER = randomUUID().toString();
    private final String COLLISION_VALIDATION_FAIL_MESSAGE = randomUUID().toString();


    private boolean wikiReferenceUpdaterCalled_update;
    private boolean permissionValidatorCalled_update;
    private boolean permissionValidatorCalled_updateProclaim;
    private boolean versionCollisionValidatorCalled;

    private boolean wikiReferenceUpdaterCalled_delete;
    private boolean permissionValidatorCalled_delete;

    private final WikiReferenceUpdater dummyWikiReferenceUpdater = new WikiReferenceUpdater() {
        @Override
        public void updateReferences(UUID documentId, Set<WikiPageTitle> referencedTitles) {
            wikiReferenceUpdaterCalled_update = true;
            if (referencedTitles.contains(UPDATE_REFERENCE_EXCEPTION_TRIGGER)) {
                throw new RuntimeException(UPDATE_REFERENCE_EXCEPTION_TRIGGERED_MESSAGE);
            }
        }

        @Override
        public void deleteReferences(UUID documentId) {
            wikiReferenceUpdaterCalled_delete = true;
        }
    };

    private final WikiPageCommandPermissionValidator dummyWikiPageCommandPermissionValidator = new WikiPageCommandPermissionValidator() {
        @Override
        public void validateUpdate(UUID actorId, WikiPage wikiPage) throws UpdatePermissionException {
            permissionValidatorCalled_update = true;
            if (actorId.equals(UPDATE_PERMISSION_EXCEPTION_TRIGGER)) {
                throw new RuntimeException(UPDATE_PERMISSION_MESSAGE);
            }
        }

        @Override
        public void validateDelete(UUID actorId, WikiPage wikiPage) {
            permissionValidatorCalled_delete = true;
            if (actorId.equals(DELETE_PERMISSION_EXCEPTION_TRIGGER)) {
                throw new RuntimeException(DELETE_PERMISSION_MESSAGE);
            }
        }

        @Override
        public void validateUpdateProclaim(UUID actorId, WikiPage wikiPage) {
            if (actorId.equals(PROCLAIM_PERMISSION_EXCEPTION_TRIGGER)) {
                throw new RuntimeException(PROCLAIM_PERMISSION_EXCEPTION_MESSAGE);
            }
            permissionValidatorCalled_updateProclaim = true;
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

    private final ApplicationEventPublisher dummyEventPublisher = new ApplicationEventPublisher() {
        @Override
        public void publishEvent(Object event) {
            publishedEvents.add(event);
        }
    };

    private final WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();

    private final WikiPageDomainService wikiPageDomainService = new WikiPageDomainService(wikiPageRepository, dummyWikiReferenceUpdater, dummyVersionCollisionValidator, dummyWikiPageCommandPermissionValidator, dummyEventPublisher);

    private WikiPage givenWikiPage;
    private UUID givenActorId;
    private WikiPageTitle givenTitle;
    private String givenContent;
    private String givenComment;
    private String givenVersionToken;
    private WikiPageTitle givenReference;
    private Set<WikiPageTitle> givenReferences;

    private List<Object> publishedEvents;

    @BeforeEach
    void init() {
        wikiReferenceUpdaterCalled_update = false;
        versionCollisionValidatorCalled = false;
        permissionValidatorCalled_update = false;
        permissionValidatorCalled_updateProclaim = false;
        wikiReferenceUpdaterCalled_delete = false;
        permissionValidatorCalled_delete = false;

        givenTitle = new WikiPageTitle(randomUUID().toString(), Namespace.NORMAL);
        givenWikiPage = wikiPageRepository.save(WikiPage.create(givenTitle.title(), givenTitle.namespace()));
        givenActorId = randomUUID();
        givenContent = randomUUID().toString();
        givenComment = randomUUID().toString();
        givenVersionToken = givenWikiPage.getVersionToken();
        givenReference = new WikiPageTitle(UUID.randomUUID().toString(), Namespace.NORMAL);
        givenReferences = Set.of();
        publishedEvents = new ArrayList<>();
    }

    void commitUpdate() {
        wikiPageDomainService.commitUpdate(givenActorId, givenTitle, givenContent, givenComment, givenVersionToken, givenReferences);
    }

    void commitUpdate_with_titleThatDoesNotExists() {
        wikiPageDomainService.commitUpdate(givenActorId, new WikiPageTitle(randomUUID().toString(), Namespace.NORMAL), givenContent, givenComment, givenVersionToken, givenReferences);
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
        assertThat(permissionValidatorCalled_update)
                .isTrue();
        assertThat(wikiReferenceUpdaterCalled_update)
                .isTrue();
    }

    @Test
    void commitUpdate_should_success_and_update_with_proper_parameters() {
        // when
        assertThatCode(this::commitUpdate)
                .doesNotThrowAnyException();

        // then
        WikiPage found = wikiPageRepository.findByTitleWithRevisionAndRawContent(givenWikiPage.getTitle(), givenWikiPage.getNamespace()).orElseThrow();

        assertThat(tuple(found.getId(), found.getWikiPageTitle(), found.getContent()))
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
        assertThat(permissionValidatorCalled_updateProclaim).isTrue();
    }

    @Test
    void proclaimUpdate_should_fail_if_permission_validating_fails() {
        assertThatThrownBy(() -> wikiPageDomainService.proclaimUpdate(PROCLAIM_PERMISSION_EXCEPTION_TRIGGER, givenTitle))
                .hasMessage(PROCLAIM_PERMISSION_EXCEPTION_MESSAGE);
    }

    @Test
    void proclaimUpdate_should_get_when_exists() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));
        saved.update(randomUUID(), randString(), randString());
        String savedContent = saved.getContent();

        // when
        WikiPage when = wikiPageDomainService.proclaimUpdate(randomUUID(), saved.getWikiPageTitle());

        // then
        assertThat(when.getContent())
                .isEqualTo(savedContent);
    }

    @Test
    void delete_should_fail_if_permission_validate_fails() {
        assertThatThrownBy(() -> wikiPageDomainService.delete(DELETE_PERMISSION_EXCEPTION_TRIGGER, givenTitle, givenComment, givenVersionToken))
                .hasMessageContaining(DELETE_PERMISSION_MESSAGE);
    }

    @Test
    void delete_should_fail_if_version_validate_fails() {
        assertThatThrownBy(() -> wikiPageDomainService.delete(givenActorId, givenTitle, givenComment, COLLISION_FAIL_TRIGGER))
                .hasMessageContaining(COLLISION_VALIDATION_FAIL_MESSAGE);
    }

    @Test
    void delete_should_success_test() {
        // when
        wikiPageDomainService.delete(givenActorId, givenTitle, givenComment, givenVersionToken);

        // then
        assertThat(permissionValidatorCalled_delete)
                .isTrue();
        assertThat(wikiReferenceUpdaterCalled_delete)
                .isTrue();
    }

    @Test
    void create_should_publish_event() {
        // when
        WikiPage wikiPage = wikiPageDomainService.create(new WikiPageTitle(randString(), Namespace.NORMAL));

        // then
        List<WikiPageCreatedEvent> events = publishedEvents.stream().filter(ev -> ev instanceof WikiPageCreatedEvent)
                .map(ev -> (WikiPageCreatedEvent) ev)
                .toList();

        assertThat(events)
                .isNotEmpty()
                .hasSize(1);

        WikiPageCreatedEvent wikiPageCreatedEvent = events.get(0);

        assertThat(tuple(wikiPageCreatedEvent.id(), wikiPageCreatedEvent.wikiPageTitle()))
                .describedAs("이벤트 객체의 내용이 적절히 설정됨.")
                .isEqualTo(tuple(wikiPage.getId(), wikiPage.getWikiPageTitle()));
    }

    @Test
    void create_should_create_when_does_not_exist() {
        String notExists = randomUUID().toString();
        WikiPageTitle nonExistTitle = new WikiPageTitle(notExists, Namespace.NORMAL);

        WikiPage wikiPage = wikiPageDomainService.create(nonExistTitle);

        assertThat(wikiPage)
                .describedAs("WikiPage 가 생성되어야함.")
                .isNotNull();

        assertThat(wikiPageRepository.findByTitleAndNamespace(notExists, Namespace.NORMAL))
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
    void create_should_fail_on_duplicate_title() {
        assertThatThrownBy(() -> wikiPageDomainService.create(givenTitle));
    }
}