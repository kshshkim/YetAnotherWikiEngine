package dev.prvt.yawiki.application.domain.innerreference;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Repository
public class InnerReferenceCustomRepositoryImpl implements InnerReferenceCustomRepository<UUID> {
    private final JPAQueryFactory queryFactory;
    static private final QInnerReference innerRef = QInnerReference.innerReference;

    public InnerReferenceCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression refererIdMatches(UUID documentId) {
        return innerRef.refererId.eq(documentId);
    }

    private BooleanExpression titleNotIn(Collection<String> titles) {
        return innerRef.referredTitle.notIn(titles);
    }

    private BooleanExpression titleIn(Collection<String> titles) {
        return innerRef.referredTitle.in(titles);
    }

    @Override
    public Set<String> findReferredTitlesByRefererId(UUID refererId) {
            return queryFactory
                    .select(innerRef.referredTitle).from(innerRef)
                    .where(refererIdMatches(refererId))
                    .stream()
                    .collect(Collectors.toSet());
    }

    @Override
    public long delete(UUID refererId, Collection<String> titlesToDelete) {
        return queryFactory
                .delete(innerRef)
                .where(
                        refererIdMatches(refererId),
                        titleIn(titlesToDelete)
                )
                .execute();
    }

    @Override
    public long deleteExcept(UUID refererId, Collection<String> titlesNotToDelete) {
        return queryFactory
                .delete(innerRef)
                .where(
                        refererIdMatches(refererId),
                        titleNotIn(titlesNotToDelete)
                )
                .execute();
    }
}
