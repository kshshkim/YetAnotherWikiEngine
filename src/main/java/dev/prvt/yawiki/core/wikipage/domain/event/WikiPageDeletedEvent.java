package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

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

    public WikiPageDeletedEvent(UUID contributorId, UUID wikiPageId, WikiPageTitle deletedTitle) {
        this(contributorId, wikiPageId, deletedTitle, null);
    }
}
