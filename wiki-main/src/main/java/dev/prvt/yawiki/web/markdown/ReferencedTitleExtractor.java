package dev.prvt.yawiki.web.markdown;

import dev.prvt.yawiki.common.model.WikiPageTitle;

import java.util.Set;

public interface ReferencedTitleExtractor {
    Set<WikiPageTitle> extractReferencedTitles(String rawMarkDown);  // 정렬된 결과를 내보내야함.
}
