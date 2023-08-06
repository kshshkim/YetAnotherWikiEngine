package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageUpdaterException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.WikiReferenceUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

/**
 * <p>WikiPage Update 를 담당하는 도메인 서비스. Update 시에 수행되어야할 부가적인 작업들을 포함함.</p>
 * <p>Validate 작업은 별개의 클래스로 분리하였으며, 애플리케이션 계층에서 트랜잭션을 적절히 나누어야함.</p>
 */
@Component
@RequiredArgsConstructor
public class WikiPageUpdater {
    private final WikiPageRepository wikiPageRepository;
    /**
     * @see dev.prvt.yawiki.core.wikireference.domain.WikiReferenceUpdaterImpl
     */
    private final WikiReferenceUpdater wikiReferenceUpdater;

    private WikiPage getWikiPage(String title) throws WikiPageUpdaterException {
        return wikiPageRepository.findByTitle(title)
                .orElseThrow(() -> new WikiPageUpdaterException("no such WikiPage"));
    }

    private void updateReferences(UUID pageId, Set<String> references) throws WikiPageUpdaterException {
        try {
            wikiReferenceUpdater.updateReferences(pageId, references);
        } catch (Exception e) {
            throw new WikiPageUpdaterException(e);
        }
    }

    /**
     * 수행되기 이전에 content 에서 reference 목록을 분리해야함.
     * @param contributorId 수정자 ID
     * @param title 제목
     * @param content 파싱되지 않은 raw 본문
     * @param comment 수정 코멘트
     * @param references 파싱되어 참조하고 있는 제목만 추출된 상태의 reference 목록
     */
    public void update(UUID contributorId, String title, String content, String comment, Set<String> references) {
        WikiPage wikiPage = getWikiPage(title);
        wikiPage.update(contributorId, comment, content);
        updateReferences(wikiPage.getId(), references);
    }
}
