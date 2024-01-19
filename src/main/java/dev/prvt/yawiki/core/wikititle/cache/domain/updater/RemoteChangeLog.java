package dev.prvt.yawiki.core.wikititle.cache.domain.updater;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleUpdateType;
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
