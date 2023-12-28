package dev.prvt.yawiki.core.wikititle.localcache.infra.updater;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.QWikiPageTitle;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.QRemoteChangeLog;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangeLog;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangesRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static dev.prvt.yawiki.core.wikititle.history.domain.QTitleHistory.titleHistory;


/**
 * TitleHistory 의존성이 존재하는 QueryDSL 구현체.
 */
@Repository
public class RemoteChangesRepositoryImpl implements RemoteChangesRepository {
    private final JPAQueryFactory queryFactory;

    public RemoteChangesRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<RemoteChangeLog> findRemoteChangesByCursor(LocalDateTime after, LocalDateTime before) {
        return queryFactory
                .select(remoteChangeLogProjection())
                .from(titleHistory)
                .where(titleHistory.createdAt.after(after), titleHistory.createdAt.before(before))
                .orderBy(titleHistory.createdAt.asc())
                .fetch();
    }

    @NotNull
    private static QRemoteChangeLog remoteChangeLogProjection() {
        return new QRemoteChangeLog(
                wikiPageTitleProjection(),
                titleHistory.createdAt,
                titleHistory.titleUpdateType
        );
    }

    @NotNull
    private static QWikiPageTitle wikiPageTitleProjection() {
        return new QWikiPageTitle(titleHistory.pageTitle, titleHistory.namespace);
    }
}
