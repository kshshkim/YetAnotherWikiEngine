package dev.prvt.yawiki.core.wikipage.application;


import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageEventPublisher;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageFactory;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.common.testutil.Fixture.randString;
import static dev.prvt.yawiki.fixture.WikiPageFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WikiPageCommandServiceImplTest {
    WikiPage givenWikiPage;

    UUID givenContributorId;

    String givenVersionToken;

    String givenComment;

    String givenContent;

    @Mock
    WikiPageRepository mockRepository;

    @Mock
    WikiPageValidator mockValidator;

    @Mock
    WikiPageEventPublisher mockEventPublisher;

    private final WikiPageMapper wikiPageMapper = new WikiPageMapper();

    private final WikiPageFactory wikiPageFactory = new WikiPageFactory();


    WikiPageCommandServiceImpl wikiPageCommandService;

    @BeforeEach
    void init() {
        wikiPageCommandService = new WikiPageCommandServiceImpl(
                mockRepository,
                mockValidator,
                mockEventPublisher,
                wikiPageMapper,
                wikiPageFactory
        );
        givenWikiPage = aNormalWikiPage();
        givenContributorId = UUID.randomUUID();
        givenVersionToken = UUID.randomUUID().toString();
        givenComment = randString();
        givenContent = randString();
    }

    @Test
    void commitUpdate() {
        // given
        HashSet<WikiPageTitle> referencedTitles = new HashSet<>();

        given(mockRepository.findByTitleAndNamespace(givenWikiPage.getTitle(), givenWikiPage.getNamespace()))
                .willReturn(Optional.of(givenWikiPage));

        // when
        wikiPageCommandService.commitUpdate(
                givenContributorId,
                givenWikiPage.getWikiPageTitle(),
                givenComment,
                givenVersionToken,
                givenContent,
                referencedTitles
        );

        // then
        verify(mockValidator).validateUpdate(givenContributorId, givenVersionToken, givenWikiPage);
        verify(mockEventPublisher).updateCommitted(givenWikiPage, referencedTitles);
        assertThat(givenWikiPage.getContent())
                .isEqualTo(givenContent);
    }

    @Captor
    ArgumentCaptor<WikiPage> wikiPageCaptor;


    @Test
    void create() {
        // given
        WikiPageTitle wikiPageTitle = aWikiPageTitle();

        // when
        wikiPageCommandService.create(givenContributorId, wikiPageTitle);

        // then
        verify(mockRepository).save(wikiPageCaptor.capture());
        verify(mockValidator).validateCreate(givenContributorId, wikiPageTitle);

        WikiPage captured = wikiPageCaptor.getValue();
        assertThat(captured.getWikiPageTitle())
                .isEqualTo(wikiPageTitle);
        assertThat(captured.isActive())
                .isFalse();
    }

    @Test
    void delete() {
        // given
        updateWikiPageRandomly(givenWikiPage);

        given(mockRepository.findByTitleAndNamespace(givenWikiPage.getTitle(), givenWikiPage.getNamespace()))
                .willReturn(Optional.of(givenWikiPage));

        // when
        wikiPageCommandService.delete(givenContributorId, givenWikiPage.getWikiPageTitle(), givenComment, givenVersionToken);

        // then
        verify(mockValidator).validateDelete(givenContributorId, givenVersionToken, givenWikiPage);
        verify(mockEventPublisher).deactivated(givenWikiPage);
        assertThat(givenWikiPage.getContent())
                .isBlank();
        assertThat(givenWikiPage.getLastModifiedBy())
                .isEqualTo(givenContributorId);
        assertThat(givenWikiPage.isActive())
                .isFalse();
    }
}