package dev.prvt.yawiki.application.domain.wikipage;

import java.util.Set;


/**
 * 위키 페이지가 업데이트 될 때, InnerReference 변동 사항을 적절히 업데이트하기 위해 사용됨.
 */
public interface WikiPageUpdatedReferenceService {
    void updateReferences(Long documentId, Set<String> referencedTitles);
}
