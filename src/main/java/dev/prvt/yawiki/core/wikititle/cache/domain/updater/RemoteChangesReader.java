package dev.prvt.yawiki.core.wikititle.cache.domain.updater;

import dev.prvt.yawiki.core.wikititle.cache.domain.updater.RemoteReadCursorProvider.ReadCursor;
import java.util.List;

public class RemoteChangesReader {

    private final RemoteReadCursorProvider remoteReadCursorProvider;

    private final RemoteChangesRepository remoteChangesRepository;


    public RemoteChangesReader(
        RemoteReadCursorProvider remoteReadCursorProvider,
        RemoteChangesRepository remoteChangesRepository
    ) {
        this.remoteReadCursorProvider = remoteReadCursorProvider;
        this.remoteChangesRepository = remoteChangesRepository;
    }

    /**
     * @return 제목 변동 내역
     */
    public List<RemoteChangeLog> readUpdated() {
        ReadCursor readCursor = remoteReadCursorProvider.getReadCursor();
        return remoteChangesRepository.findRemoteChangesByCursor(
            readCursor.after(),
            readCursor.before()
        );
    }
}
