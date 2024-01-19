package dev.prvt.yawiki.core.wikititle.cache.infra.initializer;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.cache.domain.InitialCacheData;
import dev.prvt.yawiki.core.wikititle.cache.domain.initializer.InitialCacheDataReader;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class InitialCacheDataReaderJdbcImpl implements InitialCacheDataReader {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final long readMarginInSeconds;

    public InitialCacheDataReaderJdbcImpl(NamedParameterJdbcTemplate jdbcTemplate,
        long readMarginInSeconds) {
        this.jdbcTemplate = jdbcTemplate;
        this.readMarginInSeconds = readMarginInSeconds;
    }

    @Override
    public InitialCacheData<WikiPageTitle> getInitialDataStream(LocalDateTime requestedTime) {
        LocalDateTime cacheUpdatedTime = requestedTime.minusSeconds(readMarginInSeconds);

        Map<String, LocalDateTime> params = Map.of(
//            "before", cacheUpdatedTime
        );

        Integer wikiPageCount = getWikiPageCount(params);
        Stream<WikiPageTitle> wikiPageTitleStream = getWikiPageTitleStream(params);

        return new InitialCacheData<>(wikiPageTitleStream, wikiPageCount, cacheUpdatedTime);
    }

    @Nullable
    private Integer getWikiPageCount(Map<String, LocalDateTime> params) {
        return jdbcTemplate.queryForObject(
            "select count(*) from wiki_page wp where wp.active=true",
            params,
            Integer.class
        );
    }

    private Stream<WikiPageTitle> getWikiPageTitleStream(Map<String, LocalDateTime> params) {
        return jdbcTemplate.queryForStream(
            "select wp.title, wp.namespace from wiki_page wp where wp.active=true",
            params,
            (ResultSet rs, int rowNum) -> new WikiPageTitle(
                rs.getString("title"),
                Namespace.valueOf(rs.getInt("namespace"))
            )
        );
    }
}
