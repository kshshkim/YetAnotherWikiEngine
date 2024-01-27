package dev.prvt.yawiki.titleexistence.cache.infra.updater;

import static dev.prvt.yawiki.common.util.test.CommonFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThat;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.uuid.UuidUtil;
import dev.prvt.yawiki.titleexistence.cache.domain.updater.RemoteChangeLog;
import dev.prvt.yawiki.titleexistence.cache.domain.updater.RemoteChangesRepository;
import dev.prvt.yawiki.common.model.TitleUpdateType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

@JdbcTest
@Sql("/init.sql")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RemoteChangesRepositoryImplTest {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    RemoteChangesRepository remoteChangesRepository;

    LocalDateTime now;

    void insert(UUID id, WikiPageTitle wikiPageTitle, LocalDateTime createdAt,
        TitleUpdateType titleUpdateType) {
        String sql = "insert into page_title_log(page_title_log_id, page_title, namespace, title_update_type, created_at) values(:page_title_log_id, :page_title, :namespace, :title_update_type, :created_at)";

        jdbcTemplate.update(
            sql,
            Map.of(
                "page_title_log_id", UuidUtil.asByteArray(id),
                "page_title", wikiPageTitle.title(),
                "namespace", wikiPageTitle.namespace().getIntValue(),
                "title_update_type", titleUpdateType.name(),
                "created_at", createdAt
            )
        );
    }

    /**
     * 1분, 2분, 3분 전에 생성된 내역들이 존재하는 상황을 가정. 커서는 exclusive 범위로 작동함.
     */
    @BeforeEach
    void init() {
        remoteChangesRepository = new RemoteChangesRepositoryImpl(jdbcTemplate);
        now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        LocalDateTime twoMinutesAgo = now.minusMinutes(2);
        LocalDateTime threeMinutesAgo = now.minusMinutes(3);
        WikiPageTitle titleOne = aWikiPageTitle();
        WikiPageTitle titleTwo = aWikiPageTitle();
        WikiPageTitle titleThree = aWikiPageTitle();

        insert(UUID.randomUUID(), titleOne, threeMinutesAgo, TitleUpdateType.CREATED);
        insert(UUID.randomUUID(), titleTwo, twoMinutesAgo, TitleUpdateType.CREATED);
        insert(UUID.randomUUID(), titleThree, oneMinuteAgo, TitleUpdateType.CREATED);
    }

    @Test
    @DisplayName("커서 테스트 (after < 찾아올 시점 < before)")
    void findRemoteChangesByCursor() {
        List<RemoteChangeLog> all = remoteChangesRepository.findRemoteChangesByCursor(
            now.minusMinutes(4), now);

        assertThat(all)
            .describedAs("(4분 전 < 찾아올 시점 < 현재), 모두 찾아와야함")
            .hasSize(3);

        List<RemoteChangeLog> two = remoteChangesRepository.findRemoteChangesByCursor(
            now.minusMinutes(3), now);

        assertThat(two)
            .describedAs("(3분 전 < 찾아올 시점 < 현재), 2개만 찾아와야함")
            .hasSize(2);

        List<RemoteChangeLog> one = remoteChangesRepository.findRemoteChangesByCursor(
            now.minusMinutes(10),
            now.minusMinutes(2)
        );

        assertThat(one)
            .describedAs("(10분 전 < 찾아올 시점 < 2분 전), 1개만 찾아와야함")
            .hasSize(1);

        List<RemoteChangeLog> none = remoteChangesRepository.findRemoteChangesByCursor(
            now.minusMinutes(1),
            now
        );

        assertThat(none)
            .describedAs("(1분 전 < 찾아올 시점 < 현재), 아무것도 찾아와선 안 됨.")
            .isEmpty();
    }

    @Test
    @DisplayName("정렬 테스트")
    void findRemoteChangesByCursor_order_by() {
        List<RemoteChangeLog> actual = remoteChangesRepository.findRemoteChangesByCursor(
            now.minusMinutes(4), now);

        assertThat(actual)
            .describedAs("모두 찾아와야함.")
            .hasSize(3);

        List<RemoteChangeLog> sortedByTimestamp = actual.stream()
                                             .sorted(
                                                 Comparator.comparing(RemoteChangeLog::timestamp))
                                             .toList();

        assertThat(actual)
            .describedAs("시간 순서대로 정렬된 결과를 불러왔기 때문에, 정렬 후에도 순서가 같음.")
            .containsExactlyElementsOf(sortedByTimestamp);
    }
}