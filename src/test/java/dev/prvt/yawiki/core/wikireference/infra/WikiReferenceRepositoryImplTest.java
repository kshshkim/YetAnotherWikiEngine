package dev.prvt.yawiki.core.wikireference.infra;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
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
    // todo 네임스페이스 포함 쿼리 (조회, 삭제, join) 현재 동일한 네임스페이스, 다른 제목에 대해서는 테스트가 존재함. 그러나, 다른 네임스페이스와 다른 제목에 대해선 테스트 존재 x
    // todo join 시 제목,네임스페이스 쌍으로 이루어지는지 확인해야함.
    // todo 향로 블로그 between 인덱스
    // sql or 인덱스 어떤식으로 적용되는지 확인
    // todo title namespace 받는 부분 전부 wikipagetitle 받도록 수정
    // 우선순위 하: todo wikipagetitle embed 하여 쿼리시에도 활용

    @Autowired
    EntityManager em;

    @Autowired
    WikiReferenceRepositoryImpl wikiReferenceRepository;

    private WikiPage givenWikiPage;

    private List<WikiPageTitle> givenRefTitles;

    @BeforeEach
    void initData() {
        givenWikiPage = WikiPage.create(randString());
        em.persist(givenWikiPage);
        log.info("persisted");
        List<WikiReference> refs = IntStream.range(0, 10)
                .mapToObj(i -> UUID.randomUUID().toString())
                .map(title -> new WikiReference(givenWikiPage.getId(), title, Namespace.NORMAL))
                .toList();
        givenRefTitles = refs.stream()
                .map(ref -> new WikiPageTitle(ref.getReferredTitle(), ref.getNamespace()))
                .toList();

        wikiReferenceRepository.saveAll(refs);


        em.flush();
        em.clear();
    }

    @Test
    void findReferredTitlesByRefererId() {
        // when
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found).containsAll(givenRefTitles);
        assertThat(found).hasSize(givenRefTitles.size());
    }

    @Test
    void should_find_nothing_when_document_does_not_exists() {
        // when
        Set<WikiPageTitle> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());
        // then
        assertThat(found).isEmpty();
    }


    @Test
    void should_find_nothing_when_document_is_not_active() {
        // given
        givenRefTitles.stream()
                .limit(3)
                .map(wpt -> WikiPage.create(wpt.title(), wpt.namespace()))
                .forEach(em::persist);

        em.flush();
        em.clear();

        // when
        Set<WikiPageTitle> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void should_find_only_active_wiki_pages() {
        // given
        List<WikiPage> createdWikiPage = givenRefTitles.stream()
                .limit(3)
                .map(wpt -> WikiPage.create(wpt.title(), wpt.namespace()))
                .toList();

        for (WikiPage wikiPage : createdWikiPage) {
            wikiPage.update(UUID.randomUUID(), randString(), randString());
            em.persist(wikiPage);
        }
        em.flush();
        em.clear();

        List<WikiPageTitle> activeWikiTitles = createdWikiPage.stream()
                .map(WikiPage::getWikiPageTitle)
                .toList();

        // when
        Set<WikiPageTitle> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(activeWikiTitles);
    }

    @Test
    void delete() {
        // given
        List<WikiPageTitle> toDelete = givenRefTitles.subList(0, 2);

        // when
        wikiReferenceRepository.delete(givenWikiPage.getId(), toDelete);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .describedAs("toDelete 에 포함된 요소가 제거되어야함.")
                .doesNotContainAnyElementsOf(toDelete);
    }

    @Test
    void deleteExcept() {
        // given
        List<WikiPageTitle> notToDelete = givenRefTitles.subList(0, 2);

        // when
        wikiReferenceRepository.deleteExcept(givenWikiPage.getId(), notToDelete);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
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
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenRefererId);
        assertThat(found)
                .containsExactlyInAnyOrderElementsOf(givenRefTitles);
    }

    @Test
    void findBackReferencesByWikiPageTitle_paging_not_triggered() {
        // given
        List<WikiPage> givenWikiPages = IntStream.range(0, 10)
                .mapToObj(i -> WikiPage.create(randString()))
                .toList();

        List<WikiPageTitle> givenWikiPageTitles = givenWikiPages.stream()
                .map(WikiPage::getWikiPageTitle)
                .sorted((wpt1, wpt2) -> wpt1.title().compareToIgnoreCase(wpt2.title()))
                .toList();


        givenWikiPages.forEach(em::persist);

        List<WikiReference> givenRefs = givenWikiPages.stream()
                .map(wp -> new WikiReference(wp.getId(), givenWikiPage.getTitle(), givenWikiPage.getNamespace()))
                .toList();

        wikiReferenceRepository.saveAll(givenRefs);
        em.flush();
        em.clear();

        // when
        Pageable pageable = Pageable.ofSize(givenWikiPages.size() + 10);
        Page<WikiPageTitle> result = wikiReferenceRepository.findBackReferencesByWikiPageTitle(givenWikiPage.getTitle(), givenWikiPage.getNamespace(), pageable);

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

        List<WikiPageTitle> givenWikiPageTitles = givenWikiPages.stream()
                .map(WikiPage::getWikiPageTitle)
                .sorted((wpt1, wpt2) -> wpt1.title().compareToIgnoreCase(wpt2.title()))
                .toList();


        givenWikiPages.forEach(em::persist);

        List<WikiReference> givenRefs = givenWikiPages.stream()
                .map(wp -> new WikiReference(wp.getId(), givenWikiPage.getTitle(), givenWikiPage.getNamespace()))
                .toList();

        wikiReferenceRepository.saveAll(givenRefs);
        em.flush();
        em.clear();

        // when
        int pageSize = givenWikiPages.size() / 2 + 1;

        Pageable pageable1 = Pageable.ofSize(pageSize);  // 첫번째 페이지
        Pageable pageable2 = Pageable.ofSize(pageSize).withPage(1);  // 두번째 페이지

        Page<WikiPageTitle> result1 = wikiReferenceRepository.findBackReferencesByWikiPageTitle(givenWikiPage.getTitle(), givenWikiPage.getNamespace(), pageable1);
        Page<WikiPageTitle> result2 = wikiReferenceRepository.findBackReferencesByWikiPageTitle(givenWikiPage.getTitle(), givenWikiPage.getNamespace(), pageable2);

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