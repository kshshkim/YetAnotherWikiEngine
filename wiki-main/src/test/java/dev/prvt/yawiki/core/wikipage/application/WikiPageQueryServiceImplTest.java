package dev.prvt.yawiki.core.wikipage.application;


import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageQueryRepository;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aNormalWikiPage;
import static dev.prvt.yawiki.fixture.WikiPageFixture.updateWikiPageRandomly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class WikiPageQueryServiceImplTest {

    WikiPage givenWikiPage;
    WikiPageTitle givenWikiPageTitle;
    WikiPageMapper wikiPageMapper = new WikiPageMapper();

    WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();

    @Mock
    WikiPageQueryRepository wikiPageQueryRepository;

    WikiPageQueryService wikiPageQueryService;

    @BeforeEach
    void init() {
        wikiPageQueryService = new WikiPageQueryServiceImpl(wikiPageRepository, wikiPageQueryRepository, wikiPageMapper);
        givenWikiPage = aNormalWikiPage();
        givenWikiPageTitle = givenWikiPage.getWikiPageTitle();
        wikiPageRepository.save(givenWikiPage);
    }

    @Test
    void getWikiPageDataForRead_latest_revision() {
        // given
        updateWikiPageRandomly(givenWikiPage);

        // when
        WikiPageDataForRead wikiPageForRead = wikiPageQueryService.getWikiPageDataForRead(givenWikiPageTitle);

        // then
        assertThat(wikiPageForRead)
                .isEqualTo(wikiPageMapper.mapForRead(givenWikiPage));
    }

    @Test
    void getWikiPageDataForRead_old_revision() {
        // given
        updateWikiPageRandomly(givenWikiPage);
        Revision oldRevision = givenWikiPage.getCurrentRevision();
        updateWikiPageRandomly(givenWikiPage);
        Revision latestRevision = givenWikiPage.getCurrentRevision();
        revisionQueryWillReturn(oldRevision);  // stub

        // when
        WikiPageDataForRead result = wikiPageQueryService.getWikiPageDataForRead(givenWikiPageTitle, oldRevision.getRevVersion());

        // then
        assertThat(result)
                .describedAs("지정된 버전에 해당하는 리비전을 가져오는지 검증")
                .isNotEqualTo(wikiPageMapper.mapForRead(givenWikiPage))
                .isNotEqualTo(wikiPageMapper.mapForRead(latestRevision))
                .isEqualTo(wikiPageMapper.mapForRead(oldRevision));
    }

    @Test
    void getWikiPageDataForUpdate_latest_revision() {
        // given
        updateWikiPageRandomly(givenWikiPage);

        // when
        var wikiPageForRead = wikiPageQueryService.getWikiPageDataForUpdate(givenWikiPageTitle);

        // then
        assertThat(wikiPageForRead)
                .isEqualTo(wikiPageMapper.mapForUpdate(givenWikiPage));
    }

    @Test
    void getWikiPageDataForUpdate_old_revision() {
        // given
        updateWikiPageRandomly(givenWikiPage);
        Revision oldRevision = givenWikiPage.getCurrentRevision();
        updateWikiPageRandomly(givenWikiPage);
        Revision latestRevision = givenWikiPage.getCurrentRevision();
        revisionQueryWillReturn(oldRevision);  // stub

        // when
        var result = wikiPageQueryService.getWikiPageDataForUpdate(givenWikiPageTitle, oldRevision.getRevVersion());

        // then
        assertThat(result)
                .describedAs("지정된 버전에 해당하는 리비전을 가져오는지 검증")
                .isNotEqualTo(wikiPageMapper.mapForUpdate(givenWikiPage))
                .isNotEqualTo(wikiPageMapper.mapForUpdate(latestRevision))
                .isEqualTo(wikiPageMapper.mapForUpdate(oldRevision));
    }

    @Test
    void getRevisionHistory() {
        // given
        List<Revision> givenRevisions = generateRevisions(10);
        Pageable givenPageable = Pageable.ofSize(10).withPage(0);
        PageImpl<Revision> givenPage = new PageImpl<>(givenRevisions, givenPageable, givenRevisions.size());

        given(wikiPageQueryRepository.findRevisionsByWikiPageTitle(givenWikiPageTitle, givenPageable))
                .willReturn(givenPage);

        // when
        Page<RevisionData> result = wikiPageQueryService.getRevisionHistory(givenWikiPageTitle, givenPageable);

        // then
        assertThat(result)
                .describedAs("반환 결과가 적절히 매핑되었는지 검증")
                .isEqualTo(givenPage.map(wikiPageMapper::mapFrom));
    }

    /**
     * 무작위 수정 내역을 howMany 만큼 추가하고, 버전 정보를 리스트로 반환함.
     * @param howMany 추가할 수정 내역의 숫자
     * @return 추가된 Revision 리스트
     */
    @NotNull
    private List<Revision> generateRevisions(int howMany) {
        return Stream.generate(
                        () -> {
                            updateWikiPageRandomly(givenWikiPage);
                            return givenWikiPage.getCurrentRevision();
                        }
                )
                .limit(howMany)
                .toList();
    }

    // stub
    private void revisionQueryWillReturn(Revision oldRevision) {
        given(wikiPageQueryRepository.findRevisionByWikiPageTitle(givenWikiPageTitle, oldRevision.getRevVersion()))
                .willReturn(Optional.of(oldRevision));
    }

}
