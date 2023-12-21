package dev.prvt.yawiki.core.wikireference.infra;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikireference.domain.WikiReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static dev.prvt.yawiki.fixture.WikiPageFixture.aNormalWikiPage;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class WikiReferenceRepositoryImplTest {
    @Autowired
    EntityManager em;

    @Autowired
    WikiReferenceRepositoryImpl wikiReferenceRepository;

    private final WikiPageFactory wikiPageFactory = new WikiPageFactory();

    private final int REF_LIMIT_PER_NAMESPACE = 10;
    private WikiPage givenWikiPage;

    private List<String> givenRefTitles;
    private Map<Namespace, List<WikiPageTitle>> namespaceWikiPageTitleMap;

    @BeforeEach
    void initData() {
        givenWikiPage = aNormalWikiPage();
        em.persist(givenWikiPage);
        log.info("wiki page persisted");

        givenRefTitles = Stream.generate(() -> UUID.randomUUID().toString())
                .limit(REF_LIMIT_PER_NAMESPACE)
                .toList();

        namespaceWikiPageTitleMap = new HashMap<>();

        em.flush();
        em.clear();
    }

    /**
     * (파라미터로 받은 namespace, givenRefTitles의 제목들, givenWikiPage의 id)로 WikiReference 엔티티를 생성하고 영속화함.
     */
    void initDataWithNamespace(Namespace namespace) {
        List<WikiPageTitle> list = givenRefTitles
                .stream()
                .map(title -> new WikiPageTitle(title, namespace))
                .limit(REF_LIMIT_PER_NAMESPACE)
                .toList();
        namespaceWikiPageTitleMap.put(namespace, list);
        list.stream()
                .map(wpt -> new WikiReference(givenWikiPage.getId(), wpt.title(), wpt.namespace()))
                .forEach(em::persist);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("참조 문서 ID로 참조중인 WikiPageTitle 목록 조회")
    void findReferredTitlesByRefererId() {

        // given
        // 두 가지 네임스페이스가 각각 동일한 제목을 가진 레퍼런스 데이터를 n개씩 가짐.
        initDataWithNamespace(Namespace.NORMAL);
        initDataWithNamespace(Namespace.MAIN);

        List<WikiPageTitle> wholeWikiPageTitles = namespaceWikiPageTitleMap.keySet().stream()
                .map(namespace -> namespaceWikiPageTitleMap.get(namespace))
                .flatMap(Collection::stream)
                .toList();

        // when
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found)
                .describedAs("모든 WikiPageTitle 포함.")
                .containsAll(wholeWikiPageTitles);
    }

    @Test
    void should_find_nothing_when_document_does_not_exists() {
        // when
        Set<WikiPageTitle> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(UUID.randomUUID());
        // then
        assertThat(found).isEmpty();
    }


    @Test
    @DisplayName("위키 페이지의 isActive가 false인 경우, 레퍼런스가 존재해도 아무것도 찾아오지 않음.")
    void findExistingWikiPageTitlesByRefererId_should_find_nothing_when_document_is_not_active() {
        // given
        givenRefTitles.stream()
                .limit(3)
                .map(title ->
                        {
                            WikiPage created = wikiPageFactory.create(title, Namespace.NORMAL);
                            created.delete(UUID.randomUUID(), randString());
                            return created;
                        }
                )
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
        initDataWithNamespace(Namespace.NORMAL);  // NORMAL로 초기화 (givenWikiPage.id를 참조자ID로 가지는 WikiReference들이 생성됨.

        List<WikiPage> createdWikiPage = namespaceWikiPageTitleMap.get(Namespace.NORMAL).stream()
                .limit(3)
                .map(wpt -> wikiPageFactory.create(wpt.title(), wpt.namespace()))
                .toList();

        for (WikiPage wikiPage : createdWikiPage) {
            wikiPage.update(UUID.randomUUID(), randString(), randString());  // 무작위 업데이트(isActive가 true로 설정됨.)
            em.persist(wikiPage);
        }

        em.flush();
        em.clear();

        List<WikiPageTitle> activeWikiTitles = createdWikiPage.stream()  // isActive가 true인 WikiPageTitle 리스트
                .map(WikiPage::getWikiPageTitle)
                .toList();

        // when
        Set<WikiPageTitle> found = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found)
                .describedAs("유효한 WikiPageTitle이 모두 포함됨. 유효하지 않은 WikiPageTitle은 포함되지 않음.")
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(activeWikiTitles);
    }

    @Test
    void delete() {
        // given
        initDataWithNamespace(Namespace.NORMAL);
        initDataWithNamespace(Namespace.MAIN);

        List<WikiPageTitle> toDelete = namespaceWikiPageTitleMap.get(Namespace.NORMAL).subList(0, 2);  // NORMAL 네임스페이스만 제거

        // when
        wikiReferenceRepository.delete(givenWikiPage.getId(), toDelete);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .describedAs("toDelete 에 포함된 요소가 제거되어야함.")
                .doesNotContainAnyElementsOf(toDelete)
                .describedAs("NORMAL 네임스페이스에서 2개를 지웠음. 숫자가 맞아야함.")
                .isNotEmpty()
                .hasSize(REF_LIMIT_PER_NAMESPACE*2 - 2)  // (네임스페이스-타이틀 쌍 숫자)*2 - 2
                .describedAs("title이 동일한 MAIN 네임스페이스는 지우지 않음.")
                .containsAll(namespaceWikiPageTitleMap.get(Namespace.MAIN))  // MAIN은 지우지 않았기 때문에 모두 포함.
        ;
    }

    @Test
    void deleteExcept() {
        // given
        initDataWithNamespace(Namespace.NORMAL);
        initDataWithNamespace(Namespace.MAIN);

        List<WikiPageTitle> notToDelete = namespaceWikiPageTitleMap.get(Namespace.NORMAL).subList(0, 2);

        // when
        wikiReferenceRepository.deleteExcept(givenWikiPage.getId(), notToDelete);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .describedAs("notToDelete 에 포함된 요소만 남아야함.")
                .containsExactlyInAnyOrderElementsOf(notToDelete);
    }

    @Test
    @DisplayName("새로운 Referer ID - WikiPageTitle 쌍으로 10개의 WikiReference를 insert")
    void bulkInsert() {
        // given
        UUID givenRefererId = UUID.randomUUID();
        List<WikiPageTitle> wikiPageTitles = givenRefTitles.stream()
                .map(title -> new WikiPageTitle(title, Namespace.NORMAL))
                .toList();

        // when
        wikiReferenceRepository.bulkInsert(givenRefererId, wikiPageTitles);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenRefererId);
        assertThat(found)
                .describedAs("모두 저장되어야함.")
                .containsExactlyInAnyOrderElementsOf(wikiPageTitles);
    }

    @Test
    @DisplayName("중복되는 제목이 존재하지만 네임스페이스가 다른 경우 정상적으로 insert 되어야함.")
    void bulkInsert_title_duplicate_but_namespace_is_not() {
        // given
        UUID givenRefererId = UUID.randomUUID();

        List<WikiPageTitle> normalWikiPageTitles = givenRefTitles.stream()
                .map(title -> new WikiPageTitle(title, Namespace.NORMAL))
                .toList();

        wikiReferenceRepository.bulkInsert(givenRefererId, normalWikiPageTitles);  // NORMAL 저장

        List<WikiPageTitle> mainWikiPageTitles = givenRefTitles.stream()
                .map(title -> new WikiPageTitle(title, Namespace.MAIN))
                .toList();

        List<WikiPageTitle> expecting = new ArrayList<>();
        expecting.addAll(normalWikiPageTitles);
        expecting.addAll(mainWikiPageTitles);

        // when
        wikiReferenceRepository.bulkInsert(givenRefererId, mainWikiPageTitles);

        // then
        Set<WikiPageTitle> found = wikiReferenceRepository.findReferredTitlesByRefererId(givenRefererId);

        assertThat(found)
                .describedAs("모두 저장되어야함.")
                .containsExactlyInAnyOrderElementsOf(expecting);
    }

    @Test
    @DisplayName("역레퍼런스 조회, 페이징 필요 없는 경우")
    void findBackReferencesByWikiPageTitle_paging_not_triggered() {
        // given
        List<WikiPage> givenWikiPages = IntStream.range(0, 10)
                .mapToObj(i -> aNormalWikiPage())
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
        assertThat(result.getContent())
                .describedAs("참조자 WikiPage 의 제목이 모두 포함되어야하며, 오름차순으로 정렬되어 있어야함.")
                .containsExactlyElementsOf(givenWikiPageTitles);
    }

    @Test
    void findBackReferencesByWikiPageTitle_paging_triggered() {
        // given
        List<WikiPage> givenWikiPages = IntStream.range(0, 10)
                .mapToObj(i -> aNormalWikiPage())
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