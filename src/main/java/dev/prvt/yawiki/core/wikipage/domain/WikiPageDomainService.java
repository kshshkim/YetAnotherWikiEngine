package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageCreatedEvent;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageReferenceUpdaterException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageCommandPermissionValidator;
import dev.prvt.yawiki.core.wikipage.domain.validator.VersionCollisionValidator;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.WikiReferenceUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

/**
 * <p>WikiPage Update 를 담당하는 도메인 서비스.</p>
 */
@Component
@RequiredArgsConstructor
public class WikiPageDomainService {
    private final WikiPageRepository wikiPageRepository;
    /**
     * @see dev.prvt.yawiki.core.wikireference.domain.WikiReferenceUpdaterImpl
     */
    private final WikiReferenceUpdater wikiReferenceUpdater;
    private final VersionCollisionValidator versionCollisionValidator;
    private final WikiPageCommandPermissionValidator wikiPageCommandPermissionValidator;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 문서 수정 이전에 문서 수정을 시작하기 위해 사용하는 메소드.
     * 현재는 getOrCreate 외의 별다른 행위를 하지 않음.
     * @param contributorId 편집자 ID
     * @param wikiPageTitle 수정할 문서 제목
     * @return 수정할 WikiPage 엔티티
     */
    public WikiPage proclaimUpdate(UUID contributorId, WikiPageTitle wikiPageTitle) {
        WikiPage wikiPage = getWikiPage(wikiPageTitle);
        validateProclaim(contributorId, wikiPage);
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
    public void commitUpdate(UUID contributorId, WikiPageTitle title, String content, String comment, String versionToken, Set<WikiPageTitle> references) {
        WikiPage wikiPage = getWikiPage(title);
        validateUpdate(contributorId, versionToken, wikiPage);
        wikiPage.update(contributorId, comment, content);
        updateReferences(wikiPage.getId(), references);
    }

    public void delete(UUID contributorId, WikiPageTitle wikiPageTitle, String comment, String versionToken) {
        WikiPage wikiPage = getWikiPage(wikiPageTitle);
        validateDelete(contributorId, versionToken, wikiPage);
        wikiPage.delete(contributorId, comment);
        deleteReferences(wikiPage);
    }


    private WikiPage getWikiPage(WikiPageTitle wikiPageTitle) {
        return wikiPageRepository.findByTitleAndNamespace(wikiPageTitle.title(), wikiPageTitle.namespace())
                .orElseThrow(NoSuchWikiPageException::new);
    }

    /**
     * <p>WikiPage 엔티티가 존재하지 않으면 생성, 존재하면 예외 반환.</p>
     * <p>생성 성공시 {@link WikiPageCreatedEvent} 발행.</p>
     * todo 권한 체크
     * @param wikiPageTitle 생성할 문서 제목
     * @return 생성된 WikiPage
     */
    public WikiPage create(WikiPageTitle wikiPageTitle) {
        WikiPage created = wikiPageRepository.save(WikiPage.create(wikiPageTitle.title(), wikiPageTitle.namespace()));
        applicationEventPublisher.publishEvent(new WikiPageCreatedEvent(created.getId(), created.getTitle()));
        return created;
    }

    private void updateReferences(UUID pageId, Set<WikiPageTitle> references) throws WikiPageReferenceUpdaterException {
        try {
            wikiReferenceUpdater.updateReferences(pageId, references);
        } catch (Exception e) {
            throw new WikiPageReferenceUpdaterException(e);
        }
    }

    private void deleteReferences(WikiPage wikiPage) {
        wikiReferenceUpdater.deleteReferences(wikiPage.getId());
    }

    private void validateDelete(UUID contributorId, String versionToken, WikiPage wikiPage) {
        versionCollisionValidator.validate(wikiPage, versionToken);
        wikiPageCommandPermissionValidator.validateDelete(contributorId, wikiPage);
    }

    private void validateProclaim(UUID contributorId, WikiPage wikiPage) {
        wikiPageCommandPermissionValidator.validateUpdateProclaim(contributorId, wikiPage);
    }

    private void validateUpdate(UUID contributorId, String versionToken, WikiPage wikiPage) {
        versionCollisionValidator.validate(wikiPage, versionToken);
        wikiPageCommandPermissionValidator.validateUpdate(contributorId, wikiPage);
    }
}
