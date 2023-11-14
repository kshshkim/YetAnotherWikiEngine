package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageCreatedEvent;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WikiPageDomainServiceTest {
    @Mock
    private VersionCollisionValidator mockVersionCollisionValidator;

    @Mock
    private ApplicationEventPublisher mockEventPublisher;

    @Mock
    private WikiPageCommandPermissionValidator mockPermissionValidator;

    @Mock
    private WikiReferenceUpdater mockWikiReferenceUpdater;

    @Captor
    private ArgumentCaptor<WikiPage> wikiPageCaptor;

    @Captor
    private ArgumentCaptor<Object> publishedEventsCaptor;

    private final WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();

    private WikiPageDomainService wikiPageDomainService;

    private WikiPage givenWikiPage;
    private UUID givenActorId;
    private WikiPageTitle givenTitle;
    private String givenContent;
    private String givenComment;
    private String givenVersionToken;
    private WikiPageTitle givenReference;
    private Set<WikiPageTitle> givenReferences;

    @BeforeEach
    void init() {
        wikiPageDomainService = new WikiPageDomainService(wikiPageRepository, mockWikiReferenceUpdater, mockVersionCollisionValidator, mockPermissionValidator, mockEventPublisher);

        givenTitle = new WikiPageTitle(randomUUID().toString(), Namespace.NORMAL);
        givenWikiPage = wikiPageRepository.save(WikiPage.create(givenTitle.title(), givenTitle.namespace()));
        givenActorId = randomUUID();
        givenContent = randomUUID().toString();
        givenComment = randomUUID().toString();
        givenVersionToken = givenWikiPage.getVersionToken();
        givenReference = new WikiPageTitle(UUID.randomUUID().toString(), Namespace.NORMAL);
        givenReferences = Set.of(givenReference);
    }

    private WikiPage proclaimUpdateWithGivenArguments() {
        return wikiPageDomainService.proclaimUpdate(givenActorId, givenTitle);
    }

    private void commitUpdateWithGivenParameters() {
        wikiPageDomainService.commitUpdate(givenActorId, givenTitle, givenContent, givenComment, givenVersionToken, givenReferences);
    }

    private void deleteWithGivenParameters() {
        wikiPageDomainService.delete(givenActorId, givenTitle, givenComment, givenVersionToken);
    }

    private WikiPage wikiPageThatWikiPageTitleEquals(WikiPageTitle title) {
        return argThat(wp -> wp.getWikiPageTitle().equals(title));
    }

    @NotNull
    private static RuntimeException runtimeExceptionWithRandomMessage() {
        return new RuntimeException(randString());
    }

    @Test
    void commitUpdate_should_fail_when_wiki_page_does_not_exist() {
        // given
        WikiPageTitle titleThatDoesNotExist = new WikiPageTitle(randomUUID().toString(), Namespace.NORMAL);

        // when then
        assertThatThrownBy(
                () ->
                        wikiPageDomainService.commitUpdate(
                                givenActorId,
                                titleThatDoesNotExist,
                                givenContent,
                                givenComment,
                                givenVersionToken,
                                givenReferences
                        ))
                .describedAs("해당 제목의 문서가 존재하지 않는 경우 NoSuchWikiPageException을 반환함.")
                .isInstanceOf(NoSuchWikiPageException.class)
        ;
    }

    @Test
    void commitUpdate_should_throw_WikiPageReferenceUpdaterException_when_update_failed() {
        RuntimeException givenException = runtimeExceptionWithRandomMessage();
        WikiPageTitle givenTrigger = givenReference;

        willThrow(givenException)
                .given(mockWikiReferenceUpdater).updateReferences(any(), argThat(set -> set.contains(givenTrigger)));

        assertThatThrownBy(this::commitUpdateWithGivenParameters)
                .describedAs("예외를 감싸서 반환해야함.")
                .isInstanceOf(WikiPageReferenceUpdaterException.class)
                .hasMessageContaining(givenException.getMessage())
                .hasCause(givenException)
        ;
    }

    @Test
    void commitUpdate_should_fail_update_if_update_permission_validator_fails() {
        IllegalStateException givenException = new IllegalStateException(randString());
        willThrow(givenException)
                .given(mockPermissionValidator)
                .validateEditCommit(
                        eq(givenActorId),
                        ArgumentMatchers.argThat(wp -> wp.getWikiPageTitle().equals(givenTitle))
                );

        assertThatThrownBy(this::commitUpdateWithGivenParameters)
                .describedAs("catch 되지 않은 예외가 던져짐.")
                .isSameAs(givenException)
        ;
    }

    @Test
    void commitUpdate_should_success_and_call_wikiReferenceUpdater_and_proper_wikiPagePermissionValidator_methods() {
        // given

        // when
        commitUpdateWithGivenParameters();

        // then
        verify(mockVersionCollisionValidator).validate(
                wikiPageThatWikiPageTitleEquals(givenTitle),
                eq(givenVersionToken)
        );

        verify(mockPermissionValidator).validateEditCommit(
                eq(givenActorId),
                wikiPageThatWikiPageTitleEquals(givenTitle)
        );

        verify(mockWikiReferenceUpdater).updateReferences(
                eq(givenWikiPage.getId()),
                eq(givenReferences)
        );
    }

    @Test
    void commitUpdate_should_success_and_update_with_proper_parameters() {
        // when
        commitUpdateWithGivenParameters();

        // then
        WikiPage found = wikiPageRepository.findByTitleWithRevisionAndRawContent(givenWikiPage.getTitle(), givenWikiPage.getNamespace()).orElseThrow();

        assertThat(found.getContent())
                .describedAs("업데이트가 제대로 수행됨.")
                .isNotBlank()
                .isEqualTo(givenContent);

        Revision foundCurrentRevision = found.getCurrentRevision();

        assertThat(foundCurrentRevision.getContributorId())
                .describedAs("편집자 ID가 적절히 넘어감.")
                .isEqualTo(givenActorId);
    }

    @Test
    void proclaimUpdate_should_call_validator_when_proclaim() {
        // when
        proclaimUpdateWithGivenArguments();

        // then
        verify(mockPermissionValidator).validateEditRequest(eq(givenActorId), wikiPageCaptor.capture());
        WikiPage captured = wikiPageCaptor.getValue();

        assertThat(captured.getId())
                .describedAs("validator에 WikiPage 객체가 제대로 넘어감.")
                .isEqualTo(givenWikiPage.getId());
    }

    @Test
    void proclaimUpdate_should_fail_if_permission_validating_fails() {
        // given
        String givenMessage = randString();
        IllegalStateException givenException = new IllegalStateException(givenMessage);
        willThrow(givenException)
                .given(mockPermissionValidator).validateEditRequest(givenActorId, givenWikiPage);

        // then
        assertThatThrownBy(this::proclaimUpdateWithGivenArguments)
                .isSameAs(givenException);
    }

    @Test
    void proclaimUpdate_should_success() {
        // given
        givenWikiPage.update(randomUUID(), randString(), randString());
        String savedContent = givenWikiPage.getContent();

        // when
        WikiPage result = proclaimUpdateWithGivenArguments();

        // then
        assertThat(result.getContent())
                .isEqualTo(savedContent);
    }

    @Test
    void delete_should_fail_if_permission_validate_fails() {
        RuntimeException givenException = runtimeExceptionWithRandomMessage();

        willThrow(givenException)
                .given(mockPermissionValidator)
                .validateDelete(
                        eq(givenActorId),
                        wikiPageThatWikiPageTitleEquals(givenTitle)
                );

        assertThatThrownBy(this::deleteWithGivenParameters)
                .hasMessageContaining(givenException.getMessage());
    }

    @Test
    void delete_should_fail_if_version_validate_fails() {
        // given
        RuntimeException givenException = runtimeExceptionWithRandomMessage();

        willThrow(givenException)
                .given(mockVersionCollisionValidator)
                        .validate(
                                wikiPageThatWikiPageTitleEquals(givenTitle),
                                eq(givenVersionToken)
                        );

        assertThatThrownBy(this::deleteWithGivenParameters)
                .hasMessageContaining(givenException.getMessage());
    }

    @Test
    void delete_should_success_test() {
        // when
        deleteWithGivenParameters();

        // then
        verify(mockWikiReferenceUpdater)
                .deleteReferences(givenWikiPage.getId());
        verify(mockPermissionValidator)
                .validateDelete(
                        eq(givenActorId),
                        wikiPageThatWikiPageTitleEquals(givenTitle)
                );
    }

    @Test
    void create_should_publish_event() {
        // when
        WikiPage wikiPage = wikiPageDomainService.create(new WikiPageTitle(randString(), Namespace.NORMAL));

        // then
        verify(mockEventPublisher).publishEvent(publishedEventsCaptor.capture());

        List<WikiPageCreatedEvent> events = publishedEventsCaptor.getAllValues().stream()
                .filter(ev -> ev instanceof WikiPageCreatedEvent)
                .map(ev -> (WikiPageCreatedEvent) ev)
                .toList();

        assertThat(events)
                .describedAs("문서 생성시 WikiPageCreated 이벤트가 한 번만 발행되어야함.")
                .isNotEmpty()
                .hasSize(1);

        WikiPageCreatedEvent wikiPageCreatedEvent = events.get(0);

        assertThat(List.of(wikiPageCreatedEvent.id(), wikiPageCreatedEvent.wikiPageTitle()))
                .describedAs("이벤트 객체의 내용이 적절히 설정됨.")
                .containsExactly(wikiPage.getId(), wikiPage.getWikiPageTitle());
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