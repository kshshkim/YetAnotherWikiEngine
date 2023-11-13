package dev.prvt.yawiki.core.wikipage.domain.wikireference;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.Set;
import java.util.UUID;


/**
 * 위키 페이지가 업데이트 될 때, InnerReference 변동 사항을 적절히 업데이트하기 위해 사용됨.
 */
public interface WikiReferenceUpdater {
    void updateReferences(UUID documentId, Set<WikiPageTitle> referencedTitles);
    void deleteReferences(UUID documentId);
}
