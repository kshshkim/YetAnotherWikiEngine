package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.UUID;

/**
 * 위키 문서가 생성되었다는 이벤트
 * @param id 위키 페이지의 ID
 * @param wikiPageTitle 위키 페이지의 WikiPageTitle
 */
public record WikiPageCreatedEvent(
        UUID id,
        WikiPageTitle wikiPageTitle
) {
    public WikiPageCreatedEvent {
        if (id == null) {
            throw new NullPointerException("id cannot be null");
        }
        if (wikiPageTitle == null) {
            throw new NullPointerException("wikiPageTitle cannot be null");
        }
    }
}
