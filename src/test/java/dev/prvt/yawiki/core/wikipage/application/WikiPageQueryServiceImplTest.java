package dev.prvt.yawiki.core.wikipage.application;


import dev.prvt.yawiki.Fixture;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import dev.prvt.yawiki.core.wikireference.domain.WikiReference;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceRepository;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.IntStream;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.*;

class WikiPageQueryServiceImplTest {
    boolean repositoryBackRefCalled;
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

    String givenWikiPageTitle;
    WikiPage givenWikiPage;
    List<String> givenWikiReferences;

    WikiPageQueryService wikiPageQueryService = new WikiPageQueryServiceImpl(wikiPageRepository, wikiReferenceRepository);

    @BeforeEach
    void init() {
        givenWikiPageTitle = UUID.randomUUID().toString();
        givenWikiPage = wikiPageRepository.save(WikiPage.create(givenWikiPageTitle));
        Fixture.updateWikiPageRandomly(givenWikiPage);
        givenWikiReferences = IntStream.range(0, 10)
                .mapToObj(i -> randString())
                .toList();

        repositoryBackRefCalled = false;
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
}