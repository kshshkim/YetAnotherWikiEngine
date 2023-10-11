package dev.prvt.yawiki.core.wikipage.infra.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static dev.prvt.yawiki.core.wikipage.domain.model.QRevision.revision;
import static dev.prvt.yawiki.core.wikipage.domain.model.QWikiPage.wikiPage;

@Repository
public class WikiPageQueryRepositoryImpl implements WikiPageQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final ContributorRepository contributorRepository;
    public WikiPageQueryRepositoryImpl(EntityManager em, ContributorRepository contributorRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.contributorRepository = contributorRepository;
    }

    /**
     * @param title Revision.wikiPage.title
     * @return count 쿼리 특성상 null 값을 반환하지 않음.
     */
    private long findRevisionsByTitleCount(String title) {
        //noinspection DataFlowIssue
        return queryFactory
                .select(revision.count())
                .from(revision)
                .join(wikiPage)
                    .on(revision.wikiPage.eq(wikiPage))
                .where(wikiPage.title.eq(title))
                .fetchOne();
    }

    /**
     * <p>한 번의 쿼리로 바로 가져와도 되지만, 지금 상황엔 DTO 에 포함될 정보가 변경될 가능성이 높은 관계로 편의를 위해 도메인 객체를 불러옴.</p>
     * <p>추후 최적화할것</p>
     * @param title Revision.wikiPage 의 title 값
     * @param pageable Spring Framework
     * @return
     */
    @Override
    public Page<Revision> findRevisionsByTitle(String title, Pageable pageable) {  // todo diff, contributorName

        List<Revision> content = queryFactory
                .selectFrom(revision)
                .join(wikiPage).on(revision.wikiPage.eq(wikiPage))
                .where(wikiPage.title.eq(title))
                .orderBy(revision.revVersion.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> findRevisionsByTitleCount(title));
    }

    @Override
    public Optional<Revision> findRevisionByTitleAndVersionWithRawContent(String title, int version) {
        return Optional.ofNullable(queryFactory
                .selectFrom(revision)
                .join(wikiPage).on(revision.wikiPage.eq(wikiPage))
                        .join(revision.rawContent).fetchJoin()
                .where(wikiPage.title.eq(title), revision.revVersion.eq(version))
                .fetchOne());
    }
}
