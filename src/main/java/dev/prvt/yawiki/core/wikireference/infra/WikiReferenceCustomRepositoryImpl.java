package dev.prvt.yawiki.core.wikireference.infra;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.prvt.yawiki.core.wikipage.domain.model.QWikiPage.wikiPage;
import static dev.prvt.yawiki.core.wikireference.domain.QWikiReference.wikiReference;


@Repository
public class WikiReferenceCustomRepositoryImpl implements WikiReferenceCustomRepository<UUID> {
    private final JPAQueryFactory queryFactory;

    public WikiReferenceCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Set<String> findReferredTitlesByRefererId(UUID refererId) {
        return queryFactory
                .select(wikiReference.referredTitle).from(wikiReference)
                    .where(refererIdMatches(refererId))
                .stream()
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> findExistingWikiPageTitlesByRefererId(UUID refererId) {
        return queryFactory
                .select(wikiReference.referredTitle)
                    .from(wikiReference)
                        .innerJoin(wikiPage)
                            .on(titleMatches(), wikiPageIsActive())
                    .where(refererIdMatches(refererId))
                .stream()
                .collect(Collectors.toSet());
    }

    @Override
    public long delete(UUID refererId, Collection<String> titlesToDelete) {
        return queryFactory
                .delete(wikiReference)
                    .where(
                            refererIdMatches(refererId),
                            titleIn(titlesToDelete)
                    )
                .execute();
    }

    @Override
    public long deleteExcept(UUID refererId, Collection<String> titlesNotToDelete) {
        return queryFactory
                .delete(wikiReference)
                    .where(
                            refererIdMatches(refererId),
                            titleNotIn(titlesNotToDelete)
                    )
                .execute();
    }

    private BooleanExpression titleMatches() {
        return wikiReference.referredTitle.eq(wikiPage.title);
    }

    private BooleanExpression wikiPageIsActive() {
        return wikiPage.isActive.isTrue();
    }

    private BooleanExpression refererIdMatches(UUID documentId) {
        return wikiReference.refererId.eq(documentId);
    }

    private BooleanExpression titleNotIn(Collection<String> titles) {
        return wikiReference.referredTitle.notIn(titles);
    }

    private BooleanExpression titleIn(Collection<String> titles) {
        return wikiReference.referredTitle.in(titles);
    }
}
