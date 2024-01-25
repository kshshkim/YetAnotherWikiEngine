package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.common.model.WikiPageTitle;

import java.time.LocalDateTime;
import java.util.UUID;

public record WikiPageRenamedEvent(
        UUID contributorId,
        UUID wikiPageId,
        WikiPageTitle beforeTitle,
        WikiPageTitle afterTitle,
        LocalDateTime timestamp
) {

}
