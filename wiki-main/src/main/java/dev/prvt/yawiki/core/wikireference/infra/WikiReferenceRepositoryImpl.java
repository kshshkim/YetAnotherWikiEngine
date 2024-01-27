package dev.prvt.yawiki.core.wikireference.infra;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.uuid.UuidUtil;
import dev.prvt.yawiki.core.wikireference.domain.WikiReference;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.prvt.yawiki.core.wikipage.domain.model.QWikiPage.wikiPage;
import static dev.prvt.yawiki.core.wikireference.domain.QWikiReference.wikiReference;

// todo in 절이 or 절로 바뀌었음. 관련 부분 인덱스 잘 타는지 체크해야함.
@Repository
@Slf4j
@Transactional(readOnly = true)
public class WikiReferenceRepositoryImpl implements WikiReferenceRepository {
    private final JPAQueryFactory queryFactory;
    private final WikiReferenceJpaRepository wikiReferenceJpaRepository;
    private final JdbcTemplate jdbcTemplate;


    public WikiReferenceRepositoryImpl(EntityManager em, WikiReferenceJpaRepository wikiReferenceJpaRepository, JdbcTemplate jdbcTemplate) {
        this.queryFactory = new JPAQueryFactory(em);
        this.wikiReferenceJpaRepository = wikiReferenceJpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Set<WikiPageTitle> findReferredTitlesByRefererId(UUID refererId) {
        return queryFactory
                .select(new QWikiPageTitle(wikiReference.tuple.referredTitle, wikiReference.tuple.namespace))
                    .from(wikiReference)
                    .where(wikiReferenceRefererIdMatches(refererId))
                .stream()
                .collect(Collectors.toSet());
    }

    @Override
    public Set<WikiPageTitle> findExistingWikiPageTitlesByRefererId(UUID refererId) {
        return queryFactory
                .select(new QWikiPageTitle(wikiReference.tuple.referredTitle, wikiReference.tuple.namespace))
                    .from(wikiReference)
                        .innerJoin(wikiPage)
                            .on(wikiReferenceTitleAndNamespaceMatches(wikiPage.title, wikiPage.namespace))
                    .where(
                            wikiReferenceRefererIdMatches(refererId),
                            wikiPage.active.isTrue())
                .stream()
                .collect(Collectors.toSet());
    }

    /**
     * 페이징을 위한 추가 Count 쿼리
     */
    private long backReferencesCount(String title, Namespace namespace) {
        //count 쿼리 특성상 null 값을 걱정하지 않아도 될듯함.
        //noinspection DataFlowIssue
        return queryFactory
                .select(wikiPage.count())
                .from(wikiPage)
                    .join(wikiReference)
                    .on(wikiReferenceTitleAndNamespaceMatches(wikiPage.title, wikiPage.namespace))
                .where(wikiReferenceTitleAndNamespaceMatches(title, namespace))
                .fetchOne();
    }

    /**
     * <p>지정된 페이지 크기보다 BackReference 의 숫자가 적다면, 페이징을 위한 추가 count 쿼리가 나가지 않아야함.</p>
     */
    @Override
    public Page<WikiPageTitle> findBacklinksByWikiPageTitle(String wikiPageTitle, Namespace namespace, Pageable pageable) {
        List<WikiPageTitle> content = queryFactory
                .select(new QWikiPageTitle(wikiPage.title, wikiPage.namespace))
                .from(wikiPage)
                    .join(wikiReference)
                    .on(wikiReference.tuple.refererId.eq(wikiPage.id))
                .where(
                        wikiReference.tuple.referredTitle.eq(wikiPageTitle),
                        wikiReference.tuple.namespace.eq(namespace)
                )
                .orderBy(wikiPage.title.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> backReferencesCount(wikiPageTitle, namespace));
    }

    @Override
    @Transactional
    public long delete(UUID refererId, Collection<WikiPageTitle> titlesToDelete) {
        if (titlesToDelete.isEmpty()) {
            return 0L;
        }
        return queryFactory
                .delete(wikiReference)
                    .where(
                            wikiReferenceRefererIdMatches(refererId),
                            titleIn(titlesToDelete)
                    )
                .execute();
    }

    @Override
    @Transactional
    public long deleteExcept(UUID refererId, Collection<WikiPageTitle> titlesNotToDelete) {
        return queryFactory
                .delete(wikiReference)
                    .where(
                            wikiReferenceRefererIdMatches(refererId),
                            titleNotIn(titlesNotToDelete)
                    )
                .execute();
    }

    @Override
    @Transactional
    public Iterable<WikiReference> saveAll(Iterable<WikiReference> entities) {
        return wikiReferenceJpaRepository.saveAll(entities);
    }

    /**
     * <p>ANSI 표준 SQL 문을 사용함.</p>
     * <p>SpringDataJPA 에서 제공하는 saveAll() 보다, JDBC Bulk Update 를 사용하여 구현했을 때 10 배 가량 더 빠름.</p>
     * <p>단, rewriteBatchedStatements 옵션이 활성화되지 않았으면 별 차이가 없음.</p>
     */
    @Override
    @Transactional
    public void bulkInsert(UUID refererId, List<WikiPageTitle> titles) {
        String sql = "INSERT INTO wiki_reference (referer_id, referred_title, referred_namespace) VALUES (?, ?, ?)";
        byte[] byteRefererId = UuidUtil.asByteArray(refererId);
        jdbcTemplate.batchUpdate(sql,
                titles,
                titles.size(),
                (PreparedStatement ps, WikiPageTitle title) -> {
                    ps.setBytes(1, byteRefererId);
                    ps.setString(2, title.title());
                    ps.setInt(3, title.namespace().getIntValue());
                }
        );
    }

    private BooleanExpression wikiReferenceRefererIdMatches(UUID documentId) {
        return wikiReference.tuple.refererId.eq(documentId);
    }

    private Predicate titleNotIn(Collection<WikiPageTitle> titles) {
        return titleIn(titles).not();
    }

    private Predicate titleIn(Collection<WikiPageTitle> titles) {
        BooleanBuilder builder = new BooleanBuilder();
        for (WikiPageTitle title : titles) {
            builder.or(
                    wikiReferenceTitleAndNamespaceMatches(title.title(), title.namespace())
            );
        }
        return builder;
    }

    private static BooleanExpression wikiReferenceTitleAndNamespaceMatches(String title, Namespace namespace) {
        return wikiReference.tuple.referredTitle.eq(title)
                .and(wikiReference.tuple.namespace.eq(namespace))
                ;
    }
    private static BooleanExpression wikiReferenceTitleAndNamespaceMatches(StringPath title, EnumPath<Namespace> namespace) {
        return wikiReference.tuple.referredTitle.eq(title)
                .and(wikiReference.tuple.namespace.eq(namespace))
                ;
    }
}
