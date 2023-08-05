package dev.prvt.yawiki.core.wikipage.domain.wikireference;

import java.util.Set;

public interface ReferencedTitleExtractor {
    Set<String> extractReferencedTitles(String rawMarkDown);  // 정렬된 결과를 내보내야함.
}
