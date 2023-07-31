package dev.prvt.yawiki.application.domain.innerreference;

import java.util.Collection;
import java.util.Set;

public interface InnerReferenceCustomRepository {
    /**
     * @param refererId wiki page id
     * @return Set of titles
     */
    Set<String> findReferredTitlesByRefererId(Long refererId);

    /**
     * 해당 referer 의 모든 reference 를 삭제
     * @param refererId wiki page id
     * @return affected rows
     */
    long delete(Long refererId);

    /**
     * @param refererId refererId 가 일치하는 InnerReference 중에서
     * @param titlesToDelete 삭제할 titles
     * @return affected rows
     */
    long delete(Long refererId, Collection<String> titlesToDelete);

    /**
     * @param refererId wiki page id
     * @param titlesNotToDelete 포함된 title 을 제외하고 삭제
     * @return affected rows
     */
    long deleteExcept(Long refererId, Collection<String> titlesNotToDelete);
}
