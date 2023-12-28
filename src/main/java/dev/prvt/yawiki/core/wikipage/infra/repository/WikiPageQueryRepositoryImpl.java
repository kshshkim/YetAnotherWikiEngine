package dev.prvt.yawiki.core.wikipage.infra.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.core.wikipage.domain.model.QRevision.revision;
import static dev.prvt.yawiki.core.wikipage.domain.model.QWikiPage.wikiPage;

@Repository
public class WikiPageQueryRepositoryImpl implements WikiPageQueryRepository {
    private final JPAQueryFactory queryFactory;

    public WikiPageQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Revision> findRevisionsByWikiPageTitle(WikiPageTitle wikiPageTitle, Pageable pageable) {
        List<Revision> content = queryFactory
                .selectFrom(revision)
                    .join(wikiPage).on(revision.wikiPage.eq(wikiPage)).fetchJoin()
                .where(wikiPageTitleMatches(wikiPageTitle))
                .orderBy(revision.revVersion.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> countRevision(content));
    }

    @Override
    public Page<Revision> findRevisionsByWikiPageId(UUID wikiPageId, Pageable pageable) {

        List<Revision> content = queryFactory
                .selectFrom(revision)
                .where(revision.wikiPage.id.eq(wikiPageId))
                .orderBy(revision.revVersion.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> countRevision(wikiPageId));
    }

    @Override
    public Optional<Revision> findRevisionByWikiPageTitle(WikiPageTitle wikiPageTitle, int version) {
        return queryFactory
                .selectFrom(revision)
                    .join(wikiPage).on(revision.wikiPage.eq(wikiPage)).fetchJoin()
                    .join(revision.rawContent).fetchJoin()
                .where(
                        wikiPageTitleMatches(wikiPageTitle),
                        revision.revVersion.eq(version)
                )
                .fetch()
                .stream()
                .findAny();
    }

    /**
     * Page 반환을 위해 사용되는 count 쿼리
     * @param wikiPageId WikiPage.id
     * @return count 쿼리 특성상 null 값을 반환하지 않음.
     */
    private long countRevision(UUID wikiPageId) {
        //noinspection DataFlowIssue
        return queryFactory
                .select(revision.count())
                .from(revision)
                .where(revision.wikiPage.id.eq(wikiPageId))
                .fetchOne();
    }

    private long countRevision(List<Revision> content) {
        return countRevision(getWikiPageIdFromRevisionList(content));
    }

    private static UUID getWikiPageIdFromRevisionList(List<Revision> revisionList) {
        return revisionList.get(0).getWikiPage().getId();
    }

    private static BooleanExpression wikiPageTitleMatches(WikiPageTitle wikiPageTitle) {
        return titleMatches(wikiPageTitle.title())
                .and(namespaceMatches(wikiPageTitle.namespace()));
    }

    private static BooleanExpression titleMatches(String title) {
        return wikiPage.title.eq(title);
    }

    private static BooleanExpression namespaceMatches(Namespace namespace) {
        return wikiPage.namespace.eq(namespace);
    }
}
