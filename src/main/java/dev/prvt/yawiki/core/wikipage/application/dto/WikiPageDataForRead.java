package dev.prvt.yawiki.core.wikipage.application.dto;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

/**
 * @param wikiPageTitle 제목&네임스페이스
 * @param content 본문
 */
public record WikiPageDataForRead(
        WikiPageTitle wikiPageTitle,
        String content
) {

    public String title() {
        return wikiPageTitle().title();
    }

    public Namespace namespace() {
        return wikiPageTitle().namespace();
    }
}
