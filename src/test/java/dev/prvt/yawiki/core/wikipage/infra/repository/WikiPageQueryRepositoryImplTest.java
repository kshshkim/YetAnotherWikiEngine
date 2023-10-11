package dev.prvt.yawiki.core.wikipage.infra.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static dev.prvt.yawiki.fixture.WikiPageFixture.updateWikiPageRandomly;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WikiPageQueryRepositoryImplTest {
    @Autowired
    WikiPageQueryRepositoryImpl wikiPageQueryRepository;
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    WikiPage givenWikiPage;
    List<Revision> givenRevisions;
    int TOTAL_REFS = 10;
    @BeforeEach
    void init() {
        givenWikiPage = WikiPage.create(randString());
        givenRevisions = new ArrayList<>();
        em.persist(givenWikiPage);

        for (int i = 0; i < TOTAL_REFS; i++) {
            updateWikiPageRandomly(givenWikiPage);
            givenRevisions.add(givenWikiPage.getCurrentRevision());
            em.flush();
        }

        em.clear();
        givenRevisions = givenRevisions.stream()
                .sorted((r1, r2) -> -Long.compare(r1.getRevVersion(), r2.getRevVersion()))
                .toList();

        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    @Transactional
    void findRevisionsByTitle() {
        // when
        Pageable pageable = Pageable.ofSize(TOTAL_REFS);
        Page<Revision> revisionPage = wikiPageQueryRepository.findRevisionsByTitle(givenWikiPage.getTitle(), pageable);

        // then
        assertThat(revisionPage.getTotalPages())
                .isEqualTo(1);
        assertThat(revisionPage.getNumber())
                .isEqualTo(0);
        assertThat(revisionPage.getTotalElements())
                .isEqualTo(TOTAL_REFS);

        List<Revision> content = revisionPage.getContent();
        assertThat(content.stream().map(Revision::getId))
                .describedAs("Revision 을 모두 찾아옴.")
                .containsAll(givenRevisions.stream().map(Revision::getId).toList());
        assertThat(content.stream().map(Revision::getRevVersion))
                .describedAs("내림차순으로 모두 찾아옴.")
                .containsAll(givenRevisions.stream().map(Revision::getRevVersion).toList());
    }

    @Test
    @Transactional
    void findRevisionByTitleAndVersion() {
        Random random = new Random();
        int givenVersion = random.nextInt(1, TOTAL_REFS);
        Optional<Revision> revisionByTitleAndVersion = wikiPageQueryRepository.findRevisionByTitleAndVersionWithRawContent(givenWikiPage.getTitle(), givenVersion);

        assertThat(revisionByTitleAndVersion).isPresent();
        Revision revision = revisionByTitleAndVersion.orElseThrow();
        assertThat(revision.getRevVersion())
                .isNotNull()
                .isEqualTo(givenVersion);
        assertThat(revision.getRawContent())
                .isNotNull();
        PersistenceUnitUtil persistenceUnitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertThat(persistenceUnitUtil.isLoaded(revision.getRawContent()))
                .describedAs("한 번의 쿼리로 raw content 까지 가져와야함.")
                .isTrue();
    }
}