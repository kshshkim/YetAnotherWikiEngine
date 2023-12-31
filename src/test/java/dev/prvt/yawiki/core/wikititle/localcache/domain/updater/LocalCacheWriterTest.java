package dev.prvt.yawiki.core.wikititle.localcache.domain.updater;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleUpdateType;
import dev.prvt.yawiki.core.wikititle.localcache.domain.InitialCacheData;
import dev.prvt.yawiki.core.wikititle.localcache.domain.LocalCacheStorage;
import dev.prvt.yawiki.core.wikititle.localcache.infra.updater.LocalCacheWriterImpl;
import dev.prvt.yawiki.core.wikititle.localcache.infra.LocalCacheStorageConcurrentHashMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThat;

class LocalCacheWriterTest {
    LocalCacheStorage localCacheStorage;
    LocalCacheWriter localCacheWriter;

    LocalDateTime now;

    @BeforeEach
    void init() {
        now = LocalDateTime.now();
        localCacheStorage = new LocalCacheStorageConcurrentHashMapImpl();

        localCacheStorage.init(new InitialCacheData<>(
                Stream.empty(),
                0,
                now.minusDays(10)
        ));
        localCacheWriter = new LocalCacheWriterImpl(localCacheStorage);
    }


    @Test
    void write_set_last_updated_at() {
        WikiPageTitle givenTitle = aWikiPageTitle();
        RemoteChangeLog remoteChangeLogA = new RemoteChangeLog(
                givenTitle,
                now.minusMinutes(3),
                TitleUpdateType.CREATED
        );

        RemoteChangeLog remoteChangeLogB = new RemoteChangeLog(
                givenTitle,
                now.minusMinutes(2),
                TitleUpdateType.DELETED
        );

        RemoteChangeLog remoteChangeLogC = new RemoteChangeLog(
                givenTitle,
                now.minusMinutes(1),
                TitleUpdateType.CREATED
        );

        List<RemoteChangeLog> givenLogs = List.of(remoteChangeLogA, remoteChangeLogB, remoteChangeLogC);

        // when
        localCacheWriter.write(givenLogs);

        // then
        assertThat(localCacheStorage.getLastUpdatedAt())
                .describedAs("마지막으로 가져온 로그를 기준으로 lastUpdatedAt 설정.")
                .isEqualTo(remoteChangeLogC.timestamp());
    }
    @Test
    void write_deleted() {
        WikiPageTitle givenTitle = aWikiPageTitle();
        RemoteChangeLog remoteChangeLogA = new RemoteChangeLog(
                givenTitle,
                now.minusMinutes(3),
                TitleUpdateType.CREATED
        );

        RemoteChangeLog remoteChangeLogB = new RemoteChangeLog(
                givenTitle,
                now.minusMinutes(2),
                TitleUpdateType.DELETED
        );

        List<RemoteChangeLog> givenLogs = List.of(remoteChangeLogA, remoteChangeLogB);

        // when
        localCacheWriter.write(givenLogs);

        // then
        assertThat(localCacheStorage.exists(givenTitle))
                .describedAs("생성 제거 순으로 반영되어 결과적으로 존재하지 않는 상태여야함.")
                .isFalse();
    }
    @Test
    void write_created() {
        WikiPageTitle givenTitle = aWikiPageTitle();
        RemoteChangeLog remoteChangeLogA = new RemoteChangeLog(
                givenTitle,
                now.minusMinutes(3),
                TitleUpdateType.DELETED
        );

        RemoteChangeLog remoteChangeLogB = new RemoteChangeLog(
                givenTitle,
                now.minusMinutes(2),
                TitleUpdateType.CREATED
        );

        List<RemoteChangeLog> givenLogs = List.of(remoteChangeLogA, remoteChangeLogB);

        // when
        localCacheWriter.write(givenLogs);

        // then
        assertThat(localCacheStorage.exists(givenTitle))
                .describedAs("제거 생성 순으로 반영되어 결과적으로 존재하는 상태여야함.")
                .isTrue();
    }
}