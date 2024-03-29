package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.common.model.WikiPageTitle;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record WikiPageDeletedEvent(
        UUID contributorId,
        UUID wikiPageId,
        WikiPageTitle deletedTitle,
        LocalDateTime timestamp
) {
    public WikiPageDeletedEvent {
        requireNonNull(contributorId);
        requireNonNull(wikiPageId);
        requireNonNull(deletedTitle);
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
