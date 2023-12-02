package dev.prvt.yawiki.core.wikipage.application;


import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import dev.prvt.yawiki.core.contributor.domain.MemberContributor;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.*;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageQueryRepository;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageReferenceRepository;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.IntStream;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static dev.prvt.yawiki.fixture.Fixture.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// todo 통합테스트
@ExtendWith(MockitoExtension.class)
class WikiPageQueryServiceImplTest {
    int TOTAL_REVS = 10;
    int TOTAL_CONTRIBUTORS = 5;  // CONTRIBUTOR 숫자가 더 적기 때문에 distinct 테스트 가능

    WikiPageTitle givenWikiPageTitle;
    WikiPage givenWikiPage;
    List<WikiPageTitle> givenWikiReferences;
    List<Contributor> givenContributors;
    List<Revision> givenRevisions;

    WikiPageQueryService wikiPageQueryService;

    WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();
    WikiPageMapper wikiPageMapper = new WikiPageMapper();
    @Mock
    WikiPageQueryRepository wikiPageQueryRepository;
    @Mock
    WikiPageReferenceRepository wikiPageReferenceRepository;
    @Mock
    ContributorRepository contributorRepository;


    @BeforeEach
    void init() {
        wikiPageQueryService = new WikiPageQueryServiceImpl(wikiPageRepository, wikiPageQueryRepository, wikiPageReferenceRepository, contributorRepository, wikiPageMapper);
        givenWikiPageTitle = new WikiPageTitle(UUID.randomUUID().toString(), Namespace.NORMAL);
        givenWikiPage = wikiPageRepository.save(WikiPage.create(givenWikiPageTitle.title(), givenWikiPageTitle.namespace()));
        WikiPageFixture.updateWikiPageRandomly(givenWikiPage);
        givenWikiReferences = IntStream.range(0, 10)
                .mapToObj(i -> new WikiPageTitle(randString(), Namespace.NORMAL))
                .toList();

        revisionInit();
    }

    void contributorInit() {
        givenContributors = IntStream.range(0, TOTAL_CONTRIBUTORS)
                .mapToObj(i -> (Contributor) MemberContributor.builder()
                        .id(UUID.randomUUID())
                        .memberName(UUID.randomUUID().toString())
                        .build()
                ).toList();
    }

    void revisionInit() {
        contributorInit();
        givenRevisions = new ArrayList<>();
        Revision beforeRev = null;

        for (int i = 0; i < TOTAL_REVS; i++) {
            Revision rev = Revision.builder()
                    .beforeRevision(beforeRev)
                    .contributorId(pickRandomContributorId())  // CONTRIBUTOR 숫자가 더 적기 때문에 DISTINCT 테스트 가능
                    .wikiPage(givenWikiPage)
                    .rawContent(new RawContent(randString()))
                    .comment(randString())
                    .build();
            givenRevisions.add(rev);
            beforeRev = rev;
        }
    }

    private UUID pickRandomContributorId() {
        return givenContributors.get(random.nextInt(TOTAL_CONTRIBUTORS)).getId();
    }

    // 없앨 예정인 기능이기 때문에 테스트를 제거함.
//    @Test
//    void getWikiPageDataForRead_should_success() {
//        WikiPageDataForRead wikiPage = wikiPageQueryService.getWikiPage(givenWikiPageTitle);
//
//        assertThat(wikiPage.validWikiReferences())
//                .containsExactlyInAnyOrderElementsOf(givenWikiReferences);
//
//        assertThat(wikiPage.title())
//                .isEqualTo(givenWikiPage.getTitle());
//
//        assertThat(wikiPage.content())
//                .isEqualTo(givenWikiPage.getContent());
//    }

    @Test
    void getWikiPageDataForRead_should_fail_if_wiki_page_does_not_exist() {
        WikiPageTitle nonExistTitle = new WikiPageTitle("title that does not exist " + randString(), Namespace.NORMAL);
        assertThatThrownBy(() -> wikiPageQueryService.getWikiPage(nonExistTitle))
                .isInstanceOf(NoSuchWikiPageException.class);
    }

    @Test
    void getBackReferences_build_pageable_test() {
        // given
        Pageable givenPageable = Pageable.ofSize(10).withPage(20);
        // when
        Page<WikiPageTitle> backReferences = wikiPageQueryService.getBackReferences(givenWikiPageTitle, givenPageable);
        // then
        verify(wikiPageReferenceRepository)
                .findBackReferencesByWikiPageTitle(givenWikiPageTitle.title(), givenWikiPageTitle.namespace(), givenPageable);

    }


    @Captor
    ArgumentCaptor<Collection<UUID>> contributorCaptor;

    // 통합테스트에 더 적합한 기능으로 보임. todo 통합테스트
    @Test
    void getRevisionHistory_test() {
        // given
        int givenPageNumber = random.nextInt(10);

        Pageable givenPageable = Pageable.ofSize(10).withPage(givenPageNumber);
        given(wikiPageQueryRepository.findRevisionsByWikiPageId(givenWikiPage.getId(), givenPageable))
                .willReturn(new PageImpl<>(givenRevisions, givenPageable, givenRevisions.size()));

        // when
        Page<RevisionData> revisionHistory = wikiPageQueryService.getRevisionHistory(givenWikiPageTitle, givenPageable);

        // then
        assertThat(revisionHistory.getPageable().getPageNumber())
                .describedAs("인자가 잘 넘어갔는지 확인")
                .isEqualTo(givenPageNumber);
        verify(contributorRepository).findContributorsByIds(contributorCaptor.capture());

        Collection<UUID> contributorIds = contributorCaptor.getValue();
        HashSet<UUID> distinct = new HashSet<>(contributorIds);

        assertThat(contributorIds.size())
                .describedAs("중복되는 ID를 걸러서 넣음.")
                .isEqualTo(distinct.size());
    }

    @Test
    @DisplayName("최신버전이 아닌 과거의 Revision을 불러옴")
    void getRevisionTest() {
        // given
        int givenVersion = random.nextInt(1, TOTAL_REVS - 1);  // 최신버전이 아닌 과거의 리비전
        Revision givenRevision = givenRevisions.stream()
                .filter(rv -> rv.getRevVersion().equals(givenVersion))
                .findFirst()
                .orElseThrow();
        given(wikiPageQueryRepository.findRevisionByWikiPageTitleWithRawContent(givenWikiPageTitle, givenVersion))
                .willReturn(Optional.of(givenRevision));

        // when
        WikiPageDataForRead revisionData = wikiPageQueryService.getWikiPage(givenWikiPageTitle, givenVersion);

        // then
        assertThat(revisionData)
                .isNotNull()
                .isEqualTo(new WikiPageDataForRead(givenWikiPageTitle, givenRevision.getContent()))
                .isNotEqualTo(new WikiPageDataForRead(givenWikiPageTitle, givenWikiPage.getContent()))
        ;
    }

    @Test
    void getRevision_not_found() {
        assertThatThrownBy(() -> wikiPageQueryService.getWikiPage(givenWikiPageTitle, TOTAL_REVS + 1))
                .isInstanceOf(NoSuchWikiPageException.class)
        ;
    }
}