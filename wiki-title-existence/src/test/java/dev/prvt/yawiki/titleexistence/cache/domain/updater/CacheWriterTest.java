package dev.prvt.yawiki.titleexistence.cache.domain.updater;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.model.TitleUpdateType;
import dev.prvt.yawiki.titleexistence.cache.domain.InitialCacheData;
import dev.prvt.yawiki.titleexistence.cache.domain.CacheStorage;
import dev.prvt.yawiki.titleexistence.cache.infra.updater.CacheWriterLocalCacheImpl;
import dev.prvt.yawiki.titleexistence.cache.infra.CacheStorageConcurrentHashMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static dev.prvt.yawiki.common.testutil.CommonFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThat;

class CacheWriterTest {
    CacheStorage cacheStorage;
    CacheWriter cacheWriter;

    LocalDateTime now;

    @BeforeEach
    void init() {
        now = LocalDateTime.now();
        cacheStorage = new CacheStorageConcurrentHashMapImpl();

        cacheStorage.init(new InitialCacheData<>(
                Stream.empty(),
                0,
                now.minusDays(10)
        ));
        cacheWriter = new CacheWriterLocalCacheImpl(cacheStorage);
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
        cacheWriter.write(givenLogs);

        // then
        assertThat(cacheStorage.getLastUpdatedAt())
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
        cacheWriter.write(givenLogs);

        // then
        assertThat(cacheStorage.exists(givenTitle))
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
        cacheWriter.write(givenLogs);

        // then
        assertThat(cacheStorage.exists(givenTitle))
                .describedAs("제거 생성 순으로 반영되어 결과적으로 존재하는 상태여야함.")
                .isTrue();
    }
}