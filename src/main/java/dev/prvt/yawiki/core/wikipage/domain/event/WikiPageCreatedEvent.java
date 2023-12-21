package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * 위키 문서가 생성되었다는 이벤트
 * @param id 위키 페이지의 ID
 * @param wikiPageTitle 위키 페이지의 WikiPageTitle
 */
public record WikiPageCreatedEvent(
        UUID id,
        WikiPageTitle wikiPageTitle,
        LocalDateTime timestamp
) {
    public WikiPageCreatedEvent {
        requireNonNull(id);
        requireNonNull(wikiPageTitle);
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public WikiPageCreatedEvent(UUID id, WikiPageTitle wikiPageTitle) {
        this(id, wikiPageTitle, null);
    }

    public static WikiPageCreatedEvent from(WikiPage wikiPage) {
        return new WikiPageCreatedEvent(wikiPage.getId(), wikiPage.getWikiPageTitle());
    }
}
