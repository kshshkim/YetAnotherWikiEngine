package dev.prvt.yawiki.core.wikireference.domain;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class WikiReferenceUpdater {
    private final WikiReferenceRepository wikiReferenceRepository;

    private Set<WikiPageTitle> titlesToCreate(Set<WikiPageTitle> existingRefs, Set<WikiPageTitle> updatedRefs) {
        Set<WikiPageTitle> toCreate = new HashSet<>(updatedRefs);
        toCreate.removeAll(existingRefs);
        return toCreate;
    }

    private Set<WikiPageTitle> titlesToDelete(Set<WikiPageTitle> existingRefs, Set<WikiPageTitle> updatedRefs) {
        Set<WikiPageTitle> toRemove = new HashSet<>(existingRefs);
        toRemove.removeAll(updatedRefs);
        return toRemove;
    }

    /**
     * @param refererId 참조를 포함하고 있는 WikiPage 의 ID
     * @param existingRefs 기존에 존재하고 있는 레퍼런스
     * @param updatedRefs 현재 수정된 문서에 포함된 레퍼런스
     */
    void createRefs(UUID refererId, Set<WikiPageTitle> existingRefs, Set<WikiPageTitle> updatedRefs) {
        List<WikiPageTitle> titles = titlesToCreate(existingRefs, updatedRefs)
                .stream()
                .toList();
        wikiReferenceRepository.bulkInsert(refererId, titles);
    }

    /**
     * 삭제할 레퍼런스의 숫자와 전체 레퍼런스의 숫자를 비교하여 비용이 적은 쿼리를 실행함.
     * 만약 문서의 레퍼런스가 굉장히 많을 경우, count 쿼리를 사용하는 것도 고려해봐야함.
     */
    void deleteRefs(UUID documentId, Set<WikiPageTitle> existingRefs, Set<WikiPageTitle> updatedRefs) {
        Set<WikiPageTitle> toDelete = titlesToDelete(existingRefs, updatedRefs);
        if (updatedRefs.size() < toDelete.size()) {
            // delete ... not in :updatedRefs
            wikiReferenceRepository.deleteExcept(documentId, updatedRefs);
        } else {
            // delete ... in :toDelete
            wikiReferenceRepository.delete(documentId, toDelete);
        }
    }

    /**
     * 마크다운을 파싱해서 레퍼런스 목록을 추출하는 것은 이 클래스의 책임이 아님.
     * 마크다운 파싱에는 시간이 많이 소모될 수 있음. 트랜잭션의 바깥에서 추출하여야함.
     * @param documentId 업데이트할 문서의 ID
     * @param updatedRefTitles InnerReference.referredTitle
     */
    public void updateReferences(UUID documentId, Set<WikiPageTitle> updatedRefTitles) {
        Set<WikiPageTitle> existingRefTitles = wikiReferenceRepository.findReferredTitlesByRefererId(documentId);

        deleteRefs(documentId, existingRefTitles, updatedRefTitles);
        createRefs(documentId, existingRefTitles, updatedRefTitles);
    }

    public void deleteReferences(UUID documentId) {
        wikiReferenceRepository.deleteExcept(documentId, List.of());
    }
}
