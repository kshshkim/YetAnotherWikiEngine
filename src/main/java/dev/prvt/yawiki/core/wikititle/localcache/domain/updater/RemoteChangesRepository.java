package dev.prvt.yawiki.core.wikititle.localcache.domain.updater;

import java.time.LocalDateTime;
import java.util.List;

public interface RemoteChangesRepository {
    List<RemoteChangeLog> findRemoteChangesByCursor(
            LocalDateTime after,
            LocalDateTime before
    );
}
