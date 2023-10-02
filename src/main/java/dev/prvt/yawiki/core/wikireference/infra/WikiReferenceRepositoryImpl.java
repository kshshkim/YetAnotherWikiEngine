package dev.prvt.yawiki.core.wikireference.infra;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.core.wikireference.domain.WikiReference;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.prvt.yawiki.core.wikipage.domain.model.QWikiPage.wikiPage;
import static dev.prvt.yawiki.core.wikireference.domain.QWikiReference.wikiReference;


@Repository
@Slf4j
@Transactional(readOnly = true)
public class WikiReferenceRepositoryImpl implements WikiReferenceRepository {
    private final JPAQueryFactory queryFactory;
    private final WikiReferenceJpaRepository wikiReferenceJpaRepository;
    private final JdbcTemplate jdbcTemplate;
    private final UuidGenerator uuidGenerator;


    public WikiReferenceRepositoryImpl(EntityManager em, WikiReferenceJpaRepository wikiReferenceJpaRepository, JdbcTemplate jdbcTemplate, UuidGenerator uuidGenerator) {
        this.queryFactory = new JPAQueryFactory(em);
        this.wikiReferenceJpaRepository = wikiReferenceJpaRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.uuidGenerator = uuidGenerator;
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

    /**
     * 페이징을 위한 추가 Count 쿼리
     */
    private long backReferencesCount(String referredTitle) {
        //count 쿼리 특성상 null 값을 걱정하지 않아도 될듯함.
        //noinspection DataFlowIssue
        return queryFactory
                .select(wikiPage.count())
                .from(wikiPage)
                    .join(wikiReference)
                    .on(wikiReference.refererId.eq(wikiPage.id))
                .where(wikiReference.referredTitle.eq(referredTitle))
                .fetchOne();
    }

    /**
     * <p>지정된 페이지 크기보다 BackReference 의 숫자가 적다면, 페이징을 위한 추가 count 쿼리가 나가지 않아야함.</p>
     */
    @Override
    public Page<String> findBackReferencesByWikiPageTitle(String wikiPageTitle, Pageable pageable) {
        List<String> content = queryFactory
                .select(wikiPage.title)
                .from(wikiPage)
                    .join(wikiReference)
                    .on(wikiReference.refererId.eq(wikiPage.id))
                .where(wikiReference.referredTitle.eq(wikiPageTitle))
                .orderBy(wikiPage.title.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> backReferencesCount(wikiPageTitle));
    }

    @Override
    @Transactional
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
    @Transactional
    public long deleteExcept(UUID refererId, Collection<String> titlesNotToDelete) {
        return queryFactory
                .delete(wikiReference)
                    .where(
                            refererIdMatches(refererId),
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
    public void bulkInsert(UUID refererId, List<String> titles) {
        String sql = "INSERT INTO wiki_reference (ref_id, referer_id, referred_title) VALUES (?, ?, ?)";
        byte[] byteRefererId = UUIDUtil.asByteArray(refererId);
        jdbcTemplate.batchUpdate(sql,
                titles,
                titles.size(),
                (PreparedStatement ps, String title) -> {
                    ps.setBytes(1, UUIDUtil.asByteArray(uuidGenerator.generate()));
                    ps.setBytes(2, byteRefererId);
                    ps.setString(3, title);
                }
        );
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
