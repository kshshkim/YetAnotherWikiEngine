package dev.prvt.yawiki.application.domain.innerreference;

import java.util.Collection;
import java.util.Set;

public interface InnerReferenceCustomRepository<ID> {
    /**
     * @param refererId wiki page id
     * @return Set of titles
     */
    Set<String> findReferredTitlesByRefererId(ID refererId);

    /**
     * @param refererId refererId 가 일치하는 InnerReference 중에서
     * @param titlesToDelete 삭제할 titles
     * @return affected rows
     */
    long delete(ID refererId, Collection<String> titlesToDelete);

    /**
     * @param refererId wiki page id
     * @param titlesNotToDelete 포함된 title 을 제외하고 삭제
     * @return affected rows
     */
    long deleteExcept(ID refererId, Collection<String> titlesNotToDelete);
}
