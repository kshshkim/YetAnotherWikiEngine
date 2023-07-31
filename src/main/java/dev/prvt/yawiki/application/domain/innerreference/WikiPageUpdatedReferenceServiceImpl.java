package dev.prvt.yawiki.application.domain.innerreference;

import dev.prvt.yawiki.application.domain.wikipage.WikiPageUpdatedReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@Service
@Transactional
@RequiredArgsConstructor
public class WikiPageUpdatedReferenceServiceImpl implements WikiPageUpdatedReferenceService {
    private final InnerReferenceRepository innerReferenceRepository;

    private Set<String> titlesToCreate(Set<String> existingRefs, Set<String> updatedRefs) {
        Set<String> toCreate = new HashSet<>(updatedRefs);
        toCreate.removeAll(existingRefs);
        return toCreate;
    }

    private Set<String> titlesToDelete(Set<String> existingRefs, Set<String> updatedRefs) {
        Set<String> toRemove = new HashSet<>(existingRefs);
        toRemove.removeAll(updatedRefs);
        return toRemove;
    }

    void createRefs(Long documentId, Set<String> existingRefs, Set<String> updatedRefs) {
        innerReferenceRepository.saveAll(
                titlesToCreate(existingRefs, updatedRefs).stream()
                        .map(title -> new InnerReference(documentId, title)).toList()
        );
    }

    /**
     * 삭제할 레퍼런스의 숫자와 전체 레퍼런스의 숫자를 비교하여 비용이 적은 쿼리를 실행함.
     * 만약 문서의 레퍼런스가 굉장히 많을 경우, count 쿼리를 사용하는 것도 고려해봐야함.
     */
    void deleteRefs(Long documentId, Set<String> existingRefs, Set<String> updatedRefs) {
        Set<String> toDelete = titlesToDelete(existingRefs, updatedRefs);
        if (updatedRefs.size() < toDelete.size()) {
            // delete ... not in :updatedRefs
            innerReferenceRepository.deleteExcept(documentId, updatedRefs);
        } else {
            // delete ... in :toDelete
            innerReferenceRepository.delete(documentId, toDelete);
        }
    }

    /**
     * 마크다운을 파싱해서 레퍼런스 목록을 추출하는 것은 이 클래스의 책임이 아님.
     * 마크다운 파싱에는 시간이 많이 소모될 수 있음. 트랜잭션의 바깥에서 추출하여야함.
     * @param documentId 업데이트할 문서의 ID
     * @param updatedRefTitles InnerReference.referredTitle
     */
    @Override
    public void updateReferences(Long documentId, Set<String> updatedRefTitles) {
        Set<String> existingRefTitles = innerReferenceRepository.findReferredTitlesByRefererId(documentId);
        deleteRefs(documentId, existingRefTitles, updatedRefTitles);
        createRefs(documentId, existingRefTitles, updatedRefTitles);
    }
}
