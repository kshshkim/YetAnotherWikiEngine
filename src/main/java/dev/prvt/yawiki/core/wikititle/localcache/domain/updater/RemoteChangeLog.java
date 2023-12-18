package dev.prvt.yawiki.core.wikititle.localcache.domain.updater;

import com.querydsl.core.annotations.QueryProjection;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleUpdateType;

import java.time.LocalDateTime;

public record RemoteChangeLog(
        WikiPageTitle title,
        LocalDateTime timestamp,
        TitleUpdateType changeType
) {
    @QueryProjection
    public RemoteChangeLog {
    }
}
