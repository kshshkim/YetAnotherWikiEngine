package dev.prvt.yawiki.core.wikipage.infra.event;

import dev.prvt.yawiki.core.wikipage.domain.event.*;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.WikiPageFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WikiPageEventPublisherApplicationEventPublisherImplTest {

    @Mock
    ApplicationEventPublisher mockApplicationEventPublisher;

    WikiPageEventFactory wikiPageEventFactory = new WikiPageEventFactory();

    WikiPageEventPublisher wikiPageEventPublisher;

    WikiPage givenWikiPage;

    @BeforeEach
    void init() {
        wikiPageEventPublisher = new WikiPageEventPublisherApplicationEventPublisherImpl(
                mockApplicationEventPublisher,
                wikiPageEventFactory
        );

        givenWikiPage = aNormalWikiPage();

        setWikiPageId(givenWikiPage, UUID.randomUUID());
    }

    @Test
    void created() {
        // when
        wikiPageEventPublisher.created(givenWikiPage);

        // then
        WikiPageCreatedEvent expectedToBePublished = wikiPageEventFactory.wikiPageCreatedEvent(givenWikiPage);
        verify(mockApplicationEventPublisher).publishEvent(eq(expectedToBePublished));
    }

    @Test
    void activated() {
        // given
        updateWikiPageRandomly(givenWikiPage);

        // when
        wikiPageEventPublisher.activated(givenWikiPage);

        // then
        WikiPageActivatedEvent expectedToBePublished = wikiPageEventFactory.wikiPageActivatedEvent(givenWikiPage);
        verify(mockApplicationEventPublisher).publishEvent(eq(expectedToBePublished));
    }

    @Test
    void deactivated() {
        // given
        updateWikiPageRandomly(givenWikiPage);

        // when
        wikiPageEventPublisher.deactivated(givenWikiPage);

        // then
        WikiPageDeletedEvent expectedToBePublished = wikiPageEventFactory.wikiPageDeletedEvent(givenWikiPage);
        verify(mockApplicationEventPublisher).publishEvent(eq(expectedToBePublished));
    }

    @Captor
    ArgumentCaptor<Object> mockEventPublisherCaptor;


    /**
     * 위키 페이지가 activated 상태인 경우, 즉, 수정으로 인해 active 가 false 에서 true 로 변화한 경우, activated 이벤트를 함께 발행해야함.
     */
    @Test
    @DisplayName("수정 커밋으로 인해 active 상태가 true 로 변화한 WikiPage 이벤트 발행 테스트")
    void updateCommitted_when_WikiPage_is_activated() {
        // given
        Set<WikiPageTitle> givenReferences = Stream.generate(WikiPageFixture::aWikiPageTitle)
                .limit(10)
                .collect(Collectors.toSet());
        updateWikiPageRandomly(givenWikiPage);

        // when
        wikiPageEventPublisher.updateCommitted(givenWikiPage, givenReferences);

        // then
        verify(mockApplicationEventPublisher, times(2).description("위키 페이지가 활성화된 것을 감지하여 추가 이벤트를 발행해야함."))
                .publishEvent(mockEventPublisherCaptor.capture());

        WikiPageUpdateCommittedEvent committedEvent = mockEventPublisherCaptor.getAllValues()
                .stream()
                .filter(o -> o instanceof WikiPageUpdateCommittedEvent)
                .findAny()
                .map(o -> (WikiPageUpdateCommittedEvent) o)
                .orElseThrow();

        WikiPageActivatedEvent activatedEvent = mockEventPublisherCaptor.getAllValues()
                .stream()
                .filter(o -> o instanceof WikiPageActivatedEvent)
                .findAny()
                .map(o -> (WikiPageActivatedEvent) o)
                .orElseThrow();

        assertThat(List.of(committedEvent.wikiPageTitle(), committedEvent.wikiPageId(), committedEvent.contributorId()))
                .describedAs("이벤트 내용이 적절히 구성되었는지 검증")
                .containsExactly(givenWikiPage.getWikiPageTitle(), givenWikiPage.getId(), givenWikiPage.getLastModifiedBy());

        assertThat(committedEvent.referencedTitles())
                .describedAs("이벤트에 레퍼런스 정보가 적절히 들어갔는지 검증")
                .containsExactlyElementsOf(givenReferences);

        assertThat(activatedEvent)
                .describedAs("활성화 이벤트가 적절히 구성되었는지 검증")
                .isEqualTo(wikiPageEventFactory.wikiPageActivatedEvent(givenWikiPage));
    }

    @Test
    @DisplayName("이미 활성화 상태인 WikiPage 의 수정 커밋 이벤트 발행 테스트")
    void updateCommitted_when_WikiPage_is_not_activated() {
        // given
        Set<WikiPageTitle> givenReferences = Stream.generate(WikiPageFixture::aWikiPageTitle)
                .limit(10)
                .collect(Collectors.toSet());
        updateWikiPageRandomly(givenWikiPage);
        setWikiPageActivated(givenWikiPage, false);

        // when
        wikiPageEventPublisher.updateCommitted(givenWikiPage, givenReferences);

        // then
        verify(mockApplicationEventPublisher, never().description("WikiPage 가 활성화된 상황이 아니기 때문에 activated event 발행되면 안 됨"))
                .publishEvent(WikiPageActivatedEvent.class);

        verify(mockApplicationEventPublisher, description("updateCommitted 이벤트 발행"))
                .publishEvent(mockEventPublisherCaptor.capture());


        WikiPageUpdateCommittedEvent committedEvent = (WikiPageUpdateCommittedEvent) mockEventPublisherCaptor.getValue();

        assertThat(List.of(committedEvent.wikiPageTitle(), committedEvent.wikiPageId(), committedEvent.contributorId()))
                .describedAs("이벤트 내용이 적절히 구성되었는지 검증")
                .containsExactly(givenWikiPage.getWikiPageTitle(), givenWikiPage.getId(), givenWikiPage.getLastModifiedBy());

        assertThat(committedEvent.referencedTitles())
                .describedAs("이벤트에 레퍼런스 정보가 적절히 들어갔는지 검증")
                .containsExactlyElementsOf(givenReferences);
    }
}