package dev.prvt.yawiki.core.wikipage.application.dto;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.Collection;

/**
 * @param wikiPageTitle 제목&네임스페이스
 * @param content 본문
 * @param validWikiReferences  삭제예정
 */
public record WikiPageDataForRead(
        WikiPageTitle wikiPageTitle,
        String content,
        Collection<WikiPageTitle> validWikiReferences // 삭제 예정
) {

    public String title() {
        return wikiPageTitle().title();
    }

    public Namespace namespace() {
        return wikiPageTitle().namespace();
    }
    public WikiPageDataForRead(String title, Namespace namespace, String content, Collection<WikiPageTitle> validWikiReferences) {
        this(new WikiPageTitle(title, namespace), content, validWikiReferences);
    }

    public WikiPageDataForRead(WikiPageTitle wikiPageTitle, String content, Collection<WikiPageTitle> validWikiReferences) {
        this.wikiPageTitle = wikiPageTitle;
        this.content = content;
        this.validWikiReferences = validWikiReferences;
    }
}
