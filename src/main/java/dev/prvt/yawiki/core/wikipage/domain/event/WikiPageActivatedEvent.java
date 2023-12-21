package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.time.LocalDateTime;

/**
 * 위키 문서가 활성화 상태가 될 때 발행되는 이벤트. DB에 insert 될 때 발행되는 이벤트가 아님.
 * @param wikiPageTitle 활성화 상태가 된 문서 제목
 * @param timestamp 활성화 상태가 된 시각
 */
public record WikiPageActivatedEvent(
        WikiPageTitle wikiPageTitle,
        LocalDateTime timestamp
) {
    public static WikiPageActivatedEvent from(WikiPage wikiPage) {
        return new WikiPageActivatedEvent(wikiPage.getWikiPageTitle(), wikiPage.getLastModifiedAt());
    }
}
