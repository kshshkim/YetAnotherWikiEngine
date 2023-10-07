package dev.prvt.yawiki.core.wikipage.application;


import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import dev.prvt.yawiki.core.contributor.domain.MemberContributor;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.RawContent;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageQueryRepository;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import dev.prvt.yawiki.core.wikireference.domain.WikiReference;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceRepository;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.net.InetAddress;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WikiPageQueryServiceImplTest {
    boolean repositoryBackRefCalled;
    int REVISION_TOTAL_ELEMENTS = 100;
    WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();
    WikiReferenceRepository wikiReferenceRepository = new WikiReferenceRepository() {
        @Override
        public Set<String> findReferredTitlesByRefererId(UUID refererId) {
            return null;
        }

        @Override
        public Set<String> findExistingWikiPageTitlesByRefererId(UUID refererId) {
            return new HashSet<>(givenWikiReferences);
        }

        @Override
        public Page<String> findBackReferencesByWikiPageTitle(String wikiPageTitle, Pageable pageable) {
            repositoryBackRefCalled = true;
            return new PageImpl<>(givenWikiReferences, pageable, 100);
        }

        @Override
        public long delete(UUID refererId, Collection<String> titlesToDelete) {
            return 0;
        }

        @Override
        public long deleteExcept(UUID refererId, Collection<String> titlesNotToDelete) {
            return 0;
        }

        @Override
        public Iterable<WikiReference> saveAll(Iterable<WikiReference> entities) {
            return null;
        }

        @Override
        public void bulkInsert(UUID refererId, List<String> titles) {

        }
    };
    WikiPageQueryRepository wikiPageQueryRepository = new WikiPageQueryRepository() {
        @Override
        public Page<Revision> findRevisionsByTitle(String title, Pageable pageable) {
            return new PageImpl<>(givenRevisions, pageable, REVISION_TOTAL_ELEMENTS);
        }
    };
    ContributorRepository contributorRepository = new ContributorRepository() {
        @Override
        public Stream<Contributor> findContributorsByIds(Collection<UUID> ids) {
            distinctCheckExecuted = true;
            assertThat(isDistinct(ids))
                    .describedAs("쿼리 파라미터에 distinct 를 적용해야함.")
                    .isTrue();
            return givenContributors.stream();
        }

        @Override
        public Optional<Contributor> findById(UUID id) {
            return Optional.empty();
        }

        @Override
        public <S extends Contributor> S save(S entity) {
            return null;
        }

        @Override
        public Contributor getByInetAddress(InetAddress inetAddress) {
            return null;
        }
    };
    WikiPageMapper wikiPageMapper = new WikiPageMapper();

    int TOTAL_REVS = 10;
    int TOTAL_CONTRIBUTORS = 5;  // CONTRIBUTOR 숫자가 더 적기 때문에 distinct 테스트 가능

    String givenWikiPageTitle;
    WikiPage givenWikiPage;
    List<String> givenWikiReferences;
    List<Contributor> givenContributors;
    List<Revision> givenRevisions;
    WikiPageQueryService wikiPageQueryService = new WikiPageQueryServiceImpl(wikiPageRepository, wikiPageQueryRepository, wikiReferenceRepository, contributorRepository, wikiPageMapper);
    boolean distinctCheckExecuted;
    boolean isDistinct(Collection<UUID> uuids) {
        List<UUID> list = uuids.stream().distinct().toList();
        return uuids.size() == list.size();
    }

    @BeforeEach
    void init() {
        givenWikiPageTitle = UUID.randomUUID().toString();
        givenWikiPage = wikiPageRepository.save(WikiPage.create(givenWikiPageTitle));
        WikiPageFixture.updateWikiPageRandomly(givenWikiPage);
        givenWikiReferences = IntStream.range(0, 10)
                .mapToObj(i -> randString())
                .toList();

        repositoryBackRefCalled = false;
        distinctCheckExecuted = false;
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
        //
        Random random = new Random();
        contributorInit();
        givenRevisions = new ArrayList<>();
        Revision beforeRev = null;

        for (int i = 0; i < TOTAL_REVS; i++) {
            Revision rev = Revision.builder()
                    .beforeRevision(beforeRev)
                    .contributorId(givenContributors.get(random.nextInt(TOTAL_CONTRIBUTORS)).getId())  // CONTRIBUTOR 숫자가 더 적기 때문에 DISTINCT 테스트 가능
                    .wikiPage(givenWikiPage)
                    .rawContent(new RawContent(randString()))
                    .comment(randString())
                    .build();
            givenRevisions.add(rev);
            beforeRev = rev;
        }
    }

    @Test
    void getWikiPageDataForRead_should_success() {
        WikiPageDataForRead wikiPage = wikiPageQueryService.getWikiPage(givenWikiPageTitle);

        assertThat(wikiPage.validWikiReferences())
                .containsExactlyInAnyOrderElementsOf(givenWikiReferences);

        assertThat(wikiPage.title())
                .isEqualTo(givenWikiPage.getTitle());

        assertThat(wikiPage.content())
                .isEqualTo(givenWikiPage.getContent());
    }

    @Test
    void getWikiPageDataForRead_should_fail_if_wiki_page_does_not_exist() {
        assertThatThrownBy(() -> wikiPageQueryService.getWikiPage("title that does not exist " + randString()))
                .isInstanceOf(NoSuchWikiPageException.class);
    }

    @Test
    void getBackReferences_build_pageable_test() {
        // given
        Pageable pageable = Pageable.ofSize(10).withPage(20);
        // when
        Page<String> backReferences = wikiPageQueryService.getBackReferences(givenWikiPageTitle, pageable);
        // then
        // 쿼리에 대한 검증은 repository 에서 해야함.
        assertThat(repositoryBackRefCalled)
                .describedAs("리포지토리 호출 책임 검증")
                .isTrue();
    }

    @Test
    void getRevisionHistory_test() {
        Random random = new Random();

        int givenPageNumber = random.nextInt(10);

        Page<RevisionData> revisionHistory = wikiPageQueryService.getRevisionHistory(givenWikiPageTitle, Pageable.ofSize(10).withPage(givenPageNumber));
        assertThat(revisionHistory.getPageable().getPageNumber())
                .describedAs("인자가 잘 넘어갔는지 확인")
                .isEqualTo(givenPageNumber);

        assertThat(distinctCheckExecuted)
                .describedAs("distinct 체크가 실행되었음.")
                .isTrue();
    }
}