package dev.prvt.yawiki.application.domain.innerreference;

import dev.prvt.yawiki.application.domain.wikipage.WikiPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class InnerReferenceCustomRepositoryImplTest {

    @Autowired
    InnerReferenceRepository innerReferenceRepository;

    @Autowired
    InnerReferenceCustomRepositoryImpl innerReferenceCustomRepository;

    @Autowired
    EntityManager em;


    private WikiPage givenWikiPage;

    private List<String> givenRefTitles;

    @BeforeEach
    void initData() {
        givenWikiPage = WikiPage.create(randString());
        em.persist(givenWikiPage);
        log.info("persisted");
        List<InnerReference> refs = IntStream.range(0, 10)
                .mapToObj(i -> UUID.randomUUID().toString())
                .map(title -> new InnerReference(givenWikiPage.getId(), title))
                .toList();
        givenRefTitles = refs.stream()
                .map(InnerReference::getReferredTitle)
                .toList();

        innerReferenceRepository.saveAll(refs);


        em.flush();
        em.clear();
    }

    @Test
    void findReferredTitlesByRefererId() {
        // when
        Set<String> found = innerReferenceCustomRepository.findReferredTitlesByRefererId(givenWikiPage.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found).containsAll(givenRefTitles);
        assertThat(found).hasSize(givenRefTitles.size());
    }

    @Test
    void delete() {
        // given
        List<String> toDelete = givenRefTitles.subList(0, 1);

        // when
        innerReferenceCustomRepository.delete(givenWikiPage.getId(), toDelete);

        // then
        Set<String> found = innerReferenceCustomRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .describedAs("toDelete 에 포함된 요소가 제거되어야함.")
                .doesNotContainAnyElementsOf(toDelete);
    }

    @Test
    void deleteExcept() {
        // given
        List<String> notToDelete = givenRefTitles.subList(0, 1);

        // when
        innerReferenceCustomRepository.deleteExcept(givenWikiPage.getId(), notToDelete);

        // then
        Set<String> found = innerReferenceCustomRepository.findReferredTitlesByRefererId(givenWikiPage.getId());
        assertThat(found)
                .describedAs("notToDelete 에 포함된 요소만 남아야함.")
                .containsExactlyInAnyOrderElementsOf(notToDelete);
    }
}