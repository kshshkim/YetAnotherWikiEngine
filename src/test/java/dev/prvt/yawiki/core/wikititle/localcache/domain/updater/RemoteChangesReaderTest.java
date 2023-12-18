package dev.prvt.yawiki.core.wikititle.localcache.domain.updater;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoteChangesReaderTest {
    @Mock
    RemoteChangesRepository remoteChangesRepository;

    @InjectMocks
    RemoteChangesReader remoteChangesReader;

    @Test
    void readUpdated() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime after = now.minusMinutes(60);
        LocalDateTime before = now.plusMinutes(60);
        // when
        remoteChangesReader.readUpdated(after, before);
        // then
        verify(remoteChangesRepository).findRemoteChangesByCursor(after, before);
    }
}