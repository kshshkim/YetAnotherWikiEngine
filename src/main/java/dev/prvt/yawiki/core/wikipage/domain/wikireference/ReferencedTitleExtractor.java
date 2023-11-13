package dev.prvt.yawiki.core.wikipage.domain.wikireference;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.Set;

public interface ReferencedTitleExtractor {
    Set<WikiPageTitle> extractReferencedTitles(String rawMarkDown);  // 정렬된 결과를 내보내야함.
}
