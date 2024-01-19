package dev.prvt.yawiki.core.wikititle.localcache.infra.initializer;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.uuid.impl.UUIDUtil;
import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.cache.domain.InitialCacheData;
import dev.prvt.yawiki.core.wikititle.cache.domain.initializer.InitialCacheDataReader;
import dev.prvt.yawiki.core.wikititle.cache.infra.initializer.InitialCacheDataReaderJdbcImpl;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class InitialCacheDataReaderJdbcImplTest {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    int TITLE_COUNT = 20;
    int READ_MARGIN = 60;
    List<WikiPageTitle> activeTitles = Stream.generate(WikiPageFixture::aWikiPageTitle)
                                          .limit(TITLE_COUNT).toList();
    List<WikiPageTitle> inactiveTitles = Stream.generate(WikiPageFixture::aWikiPageTitle)
                                             .limit(TITLE_COUNT).toList();

    LocalDateTime now;

    InitialCacheDataReader initialCacheDataReader;

    record TestWikiPage(
        UUID id,
        String title,
        LocalDateTime lastModifiedAt,
        Namespace namespace,
        boolean active
    ) {

    }

    @BeforeEach
    void init() {
        initialCacheDataReader = new InitialCacheDataReaderJdbcImpl(jdbcTemplate, READ_MARGIN);
        Random random = new Random();
        now = LocalDateTime.now();

        List<TestWikiPage> activeWikiPages = activeTitles.stream()
                                                 .map(
                                                     wpt -> new TestWikiPage(
                                                         UUID.randomUUID(),
                                                         wpt.title(),
                                                         now.minusSeconds(random.nextLong(READ_MARGIN + 1, 100000)),
                                                         wpt.namespace(),
                                                         true
                                                     )
                                                 )
                                                 .toList();

        List<TestWikiPage> inactiveWikiPages = inactiveTitles.stream()
                                                   .map(
                                                       wpt -> new TestWikiPage(
                                                           UUID.randomUUID(),
                                                           wpt.title(),
                                                           now.minusSeconds(random.nextLong(READ_MARGIN + 1, 100000)),
                                                           wpt.namespace(),
                                                           false
                                                       )
                                                   )
                                                   .toList();

        List<TestWikiPage> allWikiPages = new ArrayList<>();
        allWikiPages.addAll(activeWikiPages);
        allWikiPages.addAll(inactiveWikiPages);

        String sql = "insert into wiki_page(page_id, title, namespace, last_modified_at, active) values(:id, :title, :namespace, :last_modified_at, :active)";

        MapSqlParameterSource[] sqlParameterSources = allWikiPages.stream()
                                                          .map(
                                                              wp -> {
                                                                  MapSqlParameterSource param = new MapSqlParameterSource();
                                                                  param.addValue("id", UUIDUtil.asByteArray(wp.id()));
                                                                  param.addValue("title",
                                                                      wp.title());
                                                                  param.addValue("namespace",
                                                                      wp.namespace().getIntValue());
                                                                  param.addValue("last_modified_at",
                                                                      wp.lastModifiedAt());
                                                                  param.addValue("active",
                                                                      wp.active());
                                                                  return param;
                                                              })
                                                          .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, sqlParameterSources);
    }


    @Test
    void getInitialDataStream() {
        LocalDateTime now = LocalDateTime.now();

        // when
        InitialCacheData<WikiPageTitle> initialDataStream = initialCacheDataReader.getInitialDataStream(
            now);

        // then
        List<WikiPageTitle> titles = initialDataStream.stream().toList();

        assertThat(titles)
            .describedAs("비활성 상태 문서는 가져오지 않음.")
            .doesNotContainAnyElementsOf(inactiveTitles);

        assertThat(titles)
            .describedAs("오직 활성상태인 문서의 제목만 가져옴.")
            .containsExactlyInAnyOrderElementsOf(activeTitles);

        assertThat(initialDataStream.totalElements())
            .describedAs("총 원소 숫자가 적절히 입력됨")
            .isEqualTo(TITLE_COUNT);

        assertThat(initialDataStream.lastUpdatedAt())
            .describedAs("마진값을 감안하여 업데이트 수행 시점이 기록됨.")
            .isEqualTo(now.minusSeconds(READ_MARGIN));
    }
}