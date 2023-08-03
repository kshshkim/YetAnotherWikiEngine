package dev.prvt.yawiki.app.wikipage.domain;

import java.util.Set;

public interface InnerReferenceExtractor {
    Set<String> extractReferencedTitles(String rawMarkDown);  // 정렬된 결과를 내보내야함.
}
