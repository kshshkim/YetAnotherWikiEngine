package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageUpdaterException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageValidator;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.WikiReferenceUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

/**
 * <p>WikiPage Update 를 담당하는 도메인 서비스. Update 시에 수행되어야할 부가적인 작업들을 포함함.</p>
 */
@Component
@RequiredArgsConstructor
public class WikiPageDomainService {
    private final WikiPageRepository wikiPageRepository;
    /**
     * @see dev.prvt.yawiki.core.wikireference.domain.WikiReferenceUpdaterImpl
     */
    private final WikiReferenceUpdater wikiReferenceUpdater;
    private final WikiPageValidator wikiPageValidator;

    private WikiPage getWikiPage(String title) throws WikiPageUpdaterException {
        return wikiPageRepository.findByTitle(title)
                .orElseThrow(NoSuchWikiPageException::new);
    }

    private WikiPage create(String title) {
        return wikiPageRepository.save(WikiPage.create(title));
    }

    private WikiPage getOrCreate(String title) {
        return wikiPageRepository.findByTitleWithRevisionAndRawContent(title)
                .orElseGet(() -> this.create(title));
    }

    private void updateReferences(UUID pageId, Set<String> references) throws WikiPageUpdaterException {
        try {
            wikiReferenceUpdater.updateReferences(pageId, references);
        } catch (Exception e) {
            throw new WikiPageUpdaterException(e);
        }
    }

    /**
     * 문서 수정 이전에 문서 수정을 시작하기 위해 사용하는 메소드.
     * 현재는 getOrCreate 외의 별다른 행위를 하지 않음.
     * @param contributorId 편집자 ID
     * @param wikiPageTitle 수정할 문서 제목
     * @return 수정할 WikiPage 엔티티
     */
    public WikiPage proclaimUpdate(UUID contributorId, String wikiPageTitle) {
        WikiPage wikiPage = getOrCreate(wikiPageTitle);
        wikiPageValidator.validateUpdateProclaim(contributorId, wikiPage);
        return wikiPage;
    }

    /**
     * 수행되기 이전에 content 에서 reference 목록을 분리해야함.
     * @param contributorId 수정자 ID
     * @param title 제목
     * @param content 파싱되지 않은 raw 본문
     * @param comment 수정 코멘트
     * @param references 파싱되어 참조하고 있는 제목만 추출된 상태의 reference 목록
     */
    public void update(UUID contributorId, String title, String content, String comment, String versionToken, Set<String> references) {
        WikiPage wikiPage = getWikiPage(title);
        wikiPageValidator.validateUpdateCommit(contributorId, versionToken, wikiPage);
        wikiPage.update(contributorId, comment, content);
        updateReferences(wikiPage.getId(), references);
    }

}
