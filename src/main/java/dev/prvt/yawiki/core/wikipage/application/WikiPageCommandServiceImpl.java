package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.WikiPageDomainService;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.ReferencedTitleExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Set;
import java.util.UUID;

@Service
public class WikiPageCommandServiceImpl implements WikiPageCommandService {
    private final ReferencedTitleExtractor referencedTitleExtractor;
    private final WikiPageDomainService wikiPageDomainService;
    private final WikiPageMapper wikiPageMapper;
    private final TransactionTemplate transactionTemplate;

    public WikiPageCommandServiceImpl(ReferencedTitleExtractor referencedTitleExtractor, WikiPageDomainService wikiPageDomainService, PlatformTransactionManager platformTransactionManager, WikiPageMapper wikiPageMapper) {
        this.referencedTitleExtractor = referencedTitleExtractor;
        this.wikiPageDomainService = wikiPageDomainService;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.wikiPageMapper = wikiPageMapper;
    }

    /**
     * 마크다운 파싱 작업이 오래 걸릴 수 있기 때문에 트랜잭션을 분리함.
     * @param contributorId 편집자 ID
     * @param title 문서 제목
     * @param comment 편집 코멘트
     * @param versionToken 편집 충돌 방지용 토큰
     * @param content 본문
     */
    @Override
    public void commitUpdate(UUID contributorId, String title, String comment, String versionToken, String content) {
        // MarkDown 파싱은 트랜잭션의 범위 바깥에서 실행돼야함.
        Set<String> references = extractReferences(content);
        
        // 트랜잭션 시작
        executeCommit(contributorId, title, comment, content, versionToken, references);
    }

    /**
     * <p>수정 요청 데이터 반환 이전에 이루어져야할 작업이 수행됨. 이후 수정 요청에 필요한 데이터를 반환함.</p>
     * <p>수정 요청을 위해 필요한 데이터를 반환함.</p>
     * @param contributorId 편집 요청자 ID
     * @param wikiPageTitle 편집할 문서 제목
     * @return 편집에 필요한 DTO
     */
    @Override
    @Transactional
    public WikiPageDataForUpdate proclaimUpdate(UUID contributorId, String wikiPageTitle) {
        WikiPage wikiPage = wikiPageDomainService.proclaimUpdate(contributorId, wikiPageTitle);
        return wikiPageMapper.mapFrom(wikiPage);
    }

    @Override
    @Transactional
    public void create(UUID contributorId, String title) {
        wikiPageDomainService.create(title);
    }

    @Override
    @Transactional
    public void delete(UUID contributorId, String title, String comment, String versionToken) {
        wikiPageDomainService.delete(contributorId, title, comment, versionToken);
    }


    private Set<String> extractReferences(String content) {
        return referencedTitleExtractor.extractReferencedTitles(content);
    }

    private void executeCommit(UUID contributorId, String title, String comment, String content, String versionToken, Set<String> refs) {
        transactionTemplate.executeWithoutResult(
                status -> wikiPageDomainService.commitUpdate(contributorId, title, content, comment, versionToken, refs)
        );
    }
}
