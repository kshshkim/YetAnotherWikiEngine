package dev.prvt.yawiki.core.wikipage.application;


import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Set;
import java.util.UUID;

import static dev.prvt.yawiki.common.testutil.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>클린업 코드가 존재하지 않으며, 테스트 DB에 관련 데이터가 커밋됨. 문서 제목, 편집자 ID 등은 무작위 생성되어 반복 수행해도 문제가 없음.</p>
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class WikiPageCommandServiceEditCollisionTest {

    @Autowired
    WikiPageCommandService wikiPageCommandService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    WikiPageRepository wikiPageRepository;

    private WikiPageTitle normalWikiPageTitle(String title) {
        return new WikiPageTitle(title, Namespace.NORMAL);
    }

    private WikiPage getWikiPage(WikiPageTitle givenTitle) {
        return transactionTemplate.execute(
                status ->
                        wikiPageRepository.findByTitleAndNamespace(givenTitle.title(), givenTitle.namespace())
                                .orElseThrow()
        );
    }

    private String getVersionToken(WikiPageTitle givenTitle) {
        return transactionTemplate.execute(
                status -> getWikiPage(givenTitle).getVersionToken()
        );
    }

    private String getContent(WikiPageTitle givenTitle) {
        return transactionTemplate.execute(
                status -> getWikiPage(givenTitle).getContent()
        );
    }

    @Test
    @DisplayName("트랜잭션 시점에서의 편집 충돌 테스트")
    void conflict_in_transaction_time() throws InterruptedException {
        // 무작위 생성 UUID 문서 제목
        WikiPageTitle givenTitle = new WikiPageTitle(UUID.randomUUID().toString(), Namespace.NORMAL);

        String originalContent = "[[동해물]]과 [[백두산]]이 마르고 닳도록";
        Set<WikiPageTitle> originalReference = Set.of(normalWikiPageTitle("동해물"), normalWikiPageTitle("백두산"));

        String contentA = "[[동해물]]과 백두산이 마르고 닳도록 [[하느님]]이 보우하사";
        Set<WikiPageTitle> referenceA = Set.of(normalWikiPageTitle("동해물"), normalWikiPageTitle("하느님"));

        String contentB = "동해물과 [[백두산]]이 마르고 닳도록 하느님이 [[보우]]하사";
        Set<WikiPageTitle> referenceB = Set.of(normalWikiPageTitle("백두산"), normalWikiPageTitle("보우"));


        // 문서 생성 및 초기 값 업데이트
        wikiPageCommandService.create(UUID.randomUUID(), givenTitle);
        wikiPageCommandService.commitUpdate(UUID.randomUUID(), givenTitle, randString(), getVersionToken(givenTitle), originalContent, originalReference);

        // 문서 수정을 위한 버전 토큰
        String givenVersionToken = transactionTemplate.execute(status -> getWikiPage(givenTitle).getVersionToken());


        UUID contributorA = UUID.randomUUID();
        UUID contributorB = UUID.randomUUID();

        CommitUpdateWorker workerA = createWorker(contentA, givenTitle, givenVersionToken, referenceA, contributorA);
        CommitUpdateWorker workerB = createWorker(contentB, givenTitle, givenVersionToken, referenceB, contributorB);

        Thread threadA = new Thread(workerA);
        Thread threadB = new Thread(workerB);

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();

        // then
        Exception exceptionA = workerA.getException();
        Exception exceptionB = workerB.getException();

        CommitUpdateWorker successWorker;
        CommitUpdateWorker failWorker;

        // 둘 중 하나는 성공해야함
        if (exceptionA == null) {
            assertThat(exceptionB).isNotNull();
            successWorker = workerA;
            failWorker = workerB;
        } else {
            assertThat(exceptionB).isNull();
            successWorker = workerB;
            failWorker = workerA;
        }

        String updatedContent = getContent(givenTitle);

        assertThat(updatedContent)
                .describedAs("수정 이전 content 와 일치해서는 안 됨.")
                .isNotEqualTo(originalContent)
                .describedAs("성공한 트랜잭션의 content 반영되어야함.")
                .isEqualTo(successWorker.getParam().content())
                .describedAs("실패한 트랜잭션의 content 반영되지 않아야함.")
                .isNotEqualTo(failWorker.getParam().content());

        assertThat(failWorker.getException())
                .describedAs("락 확보 실패(데드락 포함)로 인한 예외가 아니어야함.")
                .isNotInstanceOf(CannotAcquireLockException.class)
                .describedAs("유니크 인덱스(page_id, rev_version)가 설정되었기 때문에, Revision 테이블에 insert 가 일어나는 시점에 실패하게 됨.")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    CommitUpdateWorker createWorker(String content, WikiPageTitle title, String versionToken, Set<WikiPageTitle> reference, UUID contributorId) {
        CommitUpdateParam commitUpdateParam = new CommitUpdateParam(contributorId, title, randString(), versionToken, content, reference);
        return CommitUpdateWorker.builder()
                .wikiPageCommandService(wikiPageCommandService)
                .param(commitUpdateParam)
                .build();
    }

    record CommitUpdateParam(
            UUID contributorId,
            WikiPageTitle wikiPageTitle,
            String comment,
            String versionToken,
            String content,
            Set<WikiPageTitle> reference
    ) {
    }


    @Getter
    static class CommitUpdateWorker implements Runnable {
        private final CommitUpdateParam param;
        private final WikiPageCommandService wikiPageCommandService;
        private Exception exception;

        @Override
        public void run() {
            try {
                wikiPageCommandService.commitUpdate(param.contributorId(), param.wikiPageTitle(), randString(), param.versionToken(), param.content(), param.reference());
            } catch (Exception e) {
                this.exception = e;
            }
        }

        @Builder
        public CommitUpdateWorker(CommitUpdateParam param, WikiPageCommandService wikiPageCommandService) {
            this.param = param;
            this.wikiPageCommandService = wikiPageCommandService;
        }
    }

}
