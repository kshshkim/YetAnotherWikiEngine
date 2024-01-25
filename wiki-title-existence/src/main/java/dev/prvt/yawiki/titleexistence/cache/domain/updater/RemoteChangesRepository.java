package dev.prvt.yawiki.titleexistence.cache.domain.updater;

import java.time.LocalDateTime;
import java.util.List;

public interface RemoteChangesRepository {
    List<RemoteChangeLog> findRemoteChangesByCursor(
            LocalDateTime after,
            LocalDateTime before
    );
}
