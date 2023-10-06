package dev.prvt.yawiki.core.wikipage.domain.event;

import java.util.UUID;

public record WikiPageCreatedEvent(
        UUID id,
        String title
) {
}
