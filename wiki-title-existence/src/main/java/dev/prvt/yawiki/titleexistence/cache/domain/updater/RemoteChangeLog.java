package dev.prvt.yawiki.titleexistence.cache.domain.updater;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.model.TitleUpdateType;
import java.time.LocalDateTime;
import java.util.Objects;

public record RemoteChangeLog(
    WikiPageTitle title,
    LocalDateTime timestamp,
    TitleUpdateType changeType
) {

    public RemoteChangeLog {
        Objects.requireNonNull(title, "title cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
        Objects.requireNonNull(changeType, "changeType cannot be null");
    }
}
