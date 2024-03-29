package dev.prvt.yawiki.titleexistence.cache.domain.updater;

import dev.prvt.yawiki.common.model.TitleUpdateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static dev.prvt.yawiki.common.util.test.CommonFixture.aWikiPageTitle;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CacheUpdaterTest {

    CacheUpdater cacheUpdater;

    @Mock
    CacheWriter cacheWriter;

    @Mock
    RemoteChangesReader remoteChangesReader;

    @BeforeEach
    void init() {
        cacheUpdater = new CacheUpdater(remoteChangesReader, cacheWriter);
    }

    @Test
    void update() {
        List<RemoteChangeLog> remoteChangeLogs = List.of(new RemoteChangeLog(
            aWikiPageTitle(), LocalDateTime.now(), TitleUpdateType.CREATED));

        given(remoteChangesReader.readUpdated())
                .willReturn(remoteChangeLogs);

        // when
        cacheUpdater.update();

        // then
        verify(cacheWriter, description("Reader 에서 읽어온 값이 정상적으로 넘어감."))
                .write(remoteChangeLogs);
    }

    @Test
    void read() {
        // when

        cacheUpdater.update();

        // then
        verify(remoteChangesReader).readUpdated();
    }
}