package dev.prvt.yawiki.core.wikireference.infra;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikireference.domain.WikiReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class WikiReferenceRepositoryImplTest {

    @Autowired
    EntityManager em;

    @Autowired
    WikiReferenceRepositoryImpl wikiReferenceRepository;

    private WikiPage givenWikiPage;

    private List<String> givenRefTitles;

    @BeforeEach
    void initData() {
        givenWikiPage = WikiPage.create(randString());
        em.persist(givenWikiPage);
        log.info("persisted");
        List<WikiReference> refs = IntStream.range(0, 10)
                .mapToObj(i -> UUID.randomUUID().toString())
                .map(title -> new WikiReference(givenWikiPage.getId(), title))
                .toList();
        givenRefTitles = refs.stream()
                .map(WikiReference::getReferredTitle)
                .toList();

        wikiReferenceRepository.saveAll(refs);


        em.flush();
        em.clear();
    }

    @Test
    void findReferredTitlesByRefererId() {
        // when
        Set<String> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found).containsAll(givenRefTitles);
        assertThat(found).hasSize(givenRefTitles.size());
    }

    @Test
    void should_find_nothing_when_document_does_not_exists() {
        // when
        Set<String> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());
        // then
        assertThat(found).isEmpty();
    }


    @Test
    void should_find_nothing_when_document_is_not_active() {
        // given
        givenRefTitles.stream()
                .limit(3)
                .map(WikiPage::create)
                .forEach(em::persist);

        em.flush();
        em.clear();

        // when
        Set<String> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void should_find_only_active_wiki_pages() {
        // given
        List<WikiPage> createdWikiPage = givenRefTitles.stream()
                .limit(3)
                .map(WikiPage::create)
                .toList();

        for (WikiPage wikiPage : createdWikiPage) {
            wikiPage.update(UUID.randomUUID(), randString(), randString());
            em.persist(wikiPage);
        }
        em.flush();
        em.clear();

        List<String> activeWikiTitles = createdWikiPage.stream()
                .map(WikiPage::getTitle)
                .toList();

        // when
        Set<String> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(activeWikiTitles);
    }

    @Test
    void delete() {
        // given
        List<String> toDelete = givenRefTitles.subList(0, 1);

        // when
        wikiReferenceRepository.delete(givenWikiPage.getId(), toDelete);

        // then
        Set<String> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .describedAs("toDelete 에 포함된 요소가 제거되어야함.")
                .doesNotContainAnyElementsOf(toDelete);
    }

    @Test
    void deleteExcept() {
        // given
        List<String> notToDelete = givenRefTitles.subList(0, 1);

        // when
        wikiReferenceRepository.deleteExcept(givenWikiPage.getId(), notToDelete);

        // then
        Set<String> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .describedAs("notToDelete 에 포함된 요소만 남아야함.")
                .containsExactlyInAnyOrderElementsOf(notToDelete);
    }

    @Test
    void bulkInsert() {
        // given
        UUID givenRefererId = UUID.randomUUID();

        // when
        wikiReferenceRepository.bulkInsert(givenRefererId, givenRefTitles);

        // then
        Set<String> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenRefererId);
        assertThat(found)
                .containsExactlyInAnyOrderElementsOf(givenRefTitles);
    }

    @Test
    void findBackReferencesByWikiPageTitle_paging_not_triggered() {
        // given
        List<WikiPage> givenWikiPages = IntStream.range(0, 10)
                .mapToObj(i -> WikiPage.create(randString()))
                .toList();

        List<String> givenWikiPageTitles = givenWikiPages.stream()
                .map(WikiPage::getTitle)
                .sorted(String::compareToIgnoreCase)
                .toList();


        givenWikiPages.forEach(em::persist);

        List<WikiReference> givenRefs = givenWikiPages.stream()
                .map(wp -> new WikiReference(wp.getId(), givenWikiPage.getTitle()))
                .toList();

        wikiReferenceRepository.saveAll(givenRefs);
        em.flush();
        em.clear();

        // when
        Pageable pageable = Pageable.ofSize(givenWikiPages.size() + 10);
        Page<String> result = wikiReferenceRepository.findBackReferencesByWikiPageTitle(givenWikiPage.getTitle(), pageable);

        // then
        assertThat(result.getTotalPages())
                .describedAs("페이지 크기가 주어진 given 값보다 크게 설정되었기 때문에, 총 페이지 개수는 10개임.")
                .isEqualTo(1);
        assertThat(result.getNumber())
                .describedAs("페이지 번호")
                .isEqualTo(0);
        String expected = Arrays.toString(givenWikiPageTitles.toArray());
        System.out.println("expected = " + expected);
        String res = Arrays.toString(result.getContent().toArray());
        System.out.println("string = " + res);
        assertThat(result.getContent())
                .describedAs("참조자 WikiPage 의 제목이 모두 포함되어야하며, 오름차순으로 정렬되어 있어야함.")
                .containsExactlyElementsOf(givenWikiPageTitles);
    }

    @Test
    void findBackReferencesByWikiPageTitle_paging_triggered() {
        // given
        List<WikiPage> givenWikiPages = IntStream.range(0, 10)
                .mapToObj(i -> WikiPage.create(randString()))
                .toList();

        List<String> givenWikiPageTitles = givenWikiPages.stream()
                .map(WikiPage::getTitle)
                .sorted(String::compareToIgnoreCase)
                .toList();


        givenWikiPages.forEach(em::persist);

        List<WikiReference> givenRefs = givenWikiPages.stream()
                .map(wp -> new WikiReference(wp.getId(), givenWikiPage.getTitle()))
                .toList();

        wikiReferenceRepository.saveAll(givenRefs);
        em.flush();
        em.clear();

        // when
        int pageSize = givenWikiPages.size() / 2 + 1;

        Pageable pageable1 = Pageable.ofSize(pageSize);  // 첫번째 페이지
        Pageable pageable2 = Pageable.ofSize(pageSize).withPage(1);  // 두번째 페이지

        Page<String> result1 = wikiReferenceRepository.findBackReferencesByWikiPageTitle(givenWikiPage.getTitle(), pageable1);
        Page<String> result2 = wikiReferenceRepository.findBackReferencesByWikiPageTitle(givenWikiPage.getTitle(), pageable2);

        // then
        // 첫번째 페이지 테스트
        assertThat(result1.getTotalPages())
                .describedAs("페이지 크기가 (주어진 값 * 1/2 + 1) 으로 설정되었기 때문에, 총 페이지 수는 2개임.")
                .isEqualTo(2);
        assertThat(result1.getNumber())
                .describedAs("페이지 번호는 0")
                .isEqualTo(0);
        assertThat(result1.getContent())
                .describedAs("참조자 WikiPage 의 제목이 모두 포함되어야하며, 오름차순으로 정렬되어 있어야함.")
                .containsExactlyElementsOf(givenWikiPageTitles.subList(0, pageSize));

        // 두번째 페이지 테스트
        assertThat(result2.getNumber())
                .describedAs("페이지 번호는 1")
                .isEqualTo(1);
        assertThat(result2.getContent())
                .describedAs("2 페이지에 있어야할 제목이 모두 포함되어야하며, 오름차순으로 정렬되어 있어야함.")
                .containsExactlyElementsOf(givenWikiPageTitles.subList(pageSize, givenWikiPages.size()));
    }

}