package dev.prvt.yawiki.core.wikititle.cache.infra.updater;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.cache.domain.updater.RemoteChangeLog;
import dev.prvt.yawiki.core.wikititle.cache.domain.updater.RemoteChangesRepository;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleUpdateType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;


/**
 * TitleHistory 의존성이 존재하는 QueryDSL 구현체.
 */
@Repository
@RequiredArgsConstructor
public class RemoteChangesRepositoryImpl implements RemoteChangesRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RemoteChangeLogRowMapper remoteChangeLogRowMapper = new RemoteChangeLogRowMapper();

    private static class RemoteChangeLogRowMapper implements RowMapper<RemoteChangeLog> {

        @Override
        public RemoteChangeLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            WikiPageTitle wikiPageTitle = new WikiPageTitle(
                rs.getString("page_title"),
                Namespace.valueOf(rs.getInt("namespace"))
            );
            return new RemoteChangeLog(
                wikiPageTitle,
                rs.getTimestamp("created_at").toLocalDateTime(),
                TitleUpdateType.valueOf(rs.getString("title_update_type"))
            );
        }
    }

    @Override
    public List<RemoteChangeLog> findRemoteChangesByCursor(LocalDateTime after, LocalDateTime before) {
        String sql = "SELECT rcl.page_title, rcl.namespace, rcl.title_update_type, rcl.created_at " +
                         "FROM page_title_log rcl " +
                         "WHERE rcl.created_at > :after AND rcl.created_at < :before " +
                         "ORDER BY rcl.created_at ASC";

        Map<String, Timestamp> params = Map.of(
            "after", Timestamp.valueOf(after),
            "before", Timestamp.valueOf(before)
        );

        return namedParameterJdbcTemplate.query(sql, params, remoteChangeLogRowMapper);
    }
}
