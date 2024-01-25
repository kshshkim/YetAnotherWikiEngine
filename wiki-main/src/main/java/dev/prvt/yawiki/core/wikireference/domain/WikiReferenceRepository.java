package dev.prvt.yawiki.core.wikireference.domain;

import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WikiReferenceRepository {
    /**
     * @param refererId wiki page id
     * @return Set of titles
     */
    Set<WikiPageTitle> findReferredTitlesByRefererId(UUID refererId);

    /**
     * @param refererId wiki page id
     * @return referer 가 참조하고 있는 문서 제목 중에, 실제로 존재하는 문서만 추려서 반환함.
     */
    Set<WikiPageTitle> findExistingWikiPageTitlesByRefererId(UUID refererId);

    /**
     * @param wikiPageTitle 참조되고 있는 문서의 제목
     * @param pageable pageable
     * @return wikiPageTitle 을 참조하고 있는 문서들의 title
     */
    Page<WikiPageTitle> findBacklinksByWikiPageTitle(String wikiPageTitle, Namespace namespace, Pageable pageable);

    /**
     * @param refererId refererId 가 일치하는 InnerReference 중에서
     * @param titlesToDelete 삭제할 titles
     * @return affected rows
     */
    long delete(UUID refererId, Collection<WikiPageTitle> titlesToDelete);

    /**
     * @param refererId wiki page id
     * @param titlesNotToDelete 포함된 title 을 제외하고 삭제
     * @return affected rows
     */
    long deleteExcept(UUID refererId, Collection<WikiPageTitle> titlesNotToDelete);

    Iterable<WikiReference> saveAll(Iterable<WikiReference> entities);

    /**
     * <p>Save all new WikiReference(refererId, title), title in titles.</p>
     * @param refererId 참조하고 있는 문서의 ID
     * @param titles 참조되고 있는 문서의 title
     */
    void bulkInsert(UUID refererId, List<WikiPageTitle> titles);
}
