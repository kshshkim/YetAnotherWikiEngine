package dev.prvt.yawiki.core.wikititle.history.domain;

import dev.prvt.yawiki.common.model.TitleUpdateType;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TitleHistoryFactory {
    public TitleHistory create(
            WikiPageTitle wikiPageTitle,
            TitleUpdateType titleUpdateType,
            LocalDateTime updatedDate
    ) {
        return TitleHistory.builder()
                .createdAt(updatedDate)
                .wikiPageTitle(wikiPageTitle)
                .titleUpdateType(titleUpdateType)
                .build();
    }
}
