package dev.prvt.yawiki.core.wikititle.localcache.domain.updater;

import dev.prvt.yawiki.core.wikititle.history.domain.TitleUpdateType;
import dev.prvt.yawiki.core.wikititle.localcache.domain.LocalCacheStorage;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.LocalCacheUpdater;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.LocalCacheWriter;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangeLog;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangesReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class LocalCacheUpdaterTest {

    LocalCacheUpdater localCacheUpdater;

    @Mock
    LocalCacheStorage localCacheStorage;

    @Mock
    LocalCacheWriter localCacheWriter;

    @Mock
    RemoteChangesReader remoteChangesReader;

    long READ_MARGIN = 100;

    @BeforeEach
    void init() {
        localCacheUpdater = new LocalCacheUpdater(localCacheStorage, remoteChangesReader, localCacheWriter, READ_MARGIN);
    }

    @Test
    void update() {
        List<RemoteChangeLog> remoteChangeLogs = List.of(new RemoteChangeLog(aWikiPageTitle(), LocalDateTime.now(), TitleUpdateType.CREATED));

        given(remoteChangesReader.readUpdated(any(), any()))
                .willReturn(remoteChangeLogs);
        given(localCacheStorage.getLastUpdatedAt())
                .willReturn(LocalDateTime.now());

        // when
        localCacheUpdater.update();

        // then
        verify(localCacheWriter, description("Reader 에서 읽어온 값이 정상적으로 넘어감."))
                .write(remoteChangeLogs);
    }

    @Captor
    ArgumentCaptor<LocalDateTime> afterCaptor;

    @Captor
    ArgumentCaptor<LocalDateTime> beforeCaptor;

    @Test
    void read() {
        // when
        LocalDateTime now = LocalDateTime.now();
        given(localCacheStorage.getLastUpdatedAt())
                .willReturn(now);

        localCacheUpdater.update();

        // then
        verify(remoteChangesReader).readUpdated(afterCaptor.capture(), beforeCaptor.capture());
        LocalDateTime captured = afterCaptor.getValue();
        assertThat(captured)
                .describedAs("설정된 마진 값을 뺀 시점부터 읽어옴.")
                .isEqualTo(now.minusSeconds(READ_MARGIN));
    }
}