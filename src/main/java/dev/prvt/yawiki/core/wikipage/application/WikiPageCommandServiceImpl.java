package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.WikiPageUpdateValidator;
import dev.prvt.yawiki.core.wikipage.domain.WikiPageUpdater;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.ReferencedTitleExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Set;
import java.util.UUID;

@Service
public class WikiPageCommandServiceImpl implements WikiPageCommandService {
    private final WikiPageUpdateValidator wikiPageUpdateValidator;
    private final ReferencedTitleExtractor referencedTitleExtractor;
    private final WikiPageUpdater wikiPageUpdater;
    private final TransactionTemplate transactionTemplate;
    private final TransactionTemplate readOnlyTransactionTemplate;

    public WikiPageCommandServiceImpl(WikiPageUpdateValidator wikiPageUpdateValidator, ReferencedTitleExtractor referencedTitleExtractor, WikiPageUpdater wikiPageUpdater, PlatformTransactionManager platformTransactionManager) {
        this.wikiPageUpdateValidator = wikiPageUpdateValidator;
        this.referencedTitleExtractor = referencedTitleExtractor;
        this.wikiPageUpdater = wikiPageUpdater;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.readOnlyTransactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.readOnlyTransactionTemplate.setReadOnly(true);
    }

    /**
     * 마크다운 파싱 작업이 오래 걸릴 수 있기 때문에 트랜잭션을 분리함.
     * 검증(트랜잭션1) -> 마크다운 파싱 -> 업데이트(트랜잭션2) -> todo 렌더링(트랜잭션3, 비동기)
     * @param contributorId 편집자 ID
     * @param title 문서 제목
     * @param comment 편집 코멘트
     * @param versionToken 편집 충돌 방지용 토큰
     * @param content 본문
     */
    @Override
    public void updateWikiPage(UUID contributorId, String title, String comment, String versionToken, String content) {
        // 트랜잭션 1 (읽기 전용) 시작
        validate(contributorId, title, versionToken);

        // 트랜잭션 1 종료, markdown parsing 작업 시작
        Set<String> references = extractReferences(content);
        
        // 트랜잭션 2 시작
        update(contributorId, title, comment, content, references);

        // todo 렌더링
    }

    @Override
    @Transactional
    public WikiPageDataForUpdate getWikiPageForUpdate(UUID contributorId, String title) {
        return null;  // todo 구현
    }

    private Set<String> extractReferences(String content) {
        return referencedTitleExtractor.extractReferencedTitles(content);
    }

    private void update(UUID contributorId, String title, String comment, String content, Set<String> refs) {
        transactionTemplate.executeWithoutResult(
                status -> wikiPageUpdater.update(contributorId, title, content, comment, refs)
        );
    }

    private void validate(UUID contributorId, String title, String versionToken) {
        readOnlyTransactionTemplate.executeWithoutResult(status -> {
            wikiPageUpdateValidator.validate(contributorId, title, versionToken);
        });
    }

}
