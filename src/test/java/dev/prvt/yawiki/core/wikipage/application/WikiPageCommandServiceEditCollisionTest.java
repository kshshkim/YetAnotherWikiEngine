package dev.prvt.yawiki.core.wikipage.application;


import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageCommandPermissionValidator;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class WikiPageCommandServiceEditCollisionTest {

    @MockBean
    ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    WikiPageCommandPermissionValidator wikiPageCommandPermissionValidator;

    @Autowired
    WikiPageCommandService wikiPageCommandService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @PersistenceContext
    EntityManager em;

    WikiPageTitle givenTitle;

    @BeforeEach
    void init() {
        givenTitle = new WikiPageTitle(randString(), Namespace.NORMAL);
        wikiPageCommandService.create(UUID.randomUUID(), givenTitle);
        WikiPageDataForUpdate wikiPageDataForUpdate = wikiPageCommandService.proclaimUpdate(UUID.randomUUID(), givenTitle);
        wikiPageCommandService.commitUpdate(UUID.randomUUID(), givenTitle, randString(), wikiPageDataForUpdate.versionToken(), randString(), new HashSet<>());
    }

    @AfterEach
    void clean() {
        transactionTemplate.executeWithoutResult(
                status -> em.createQuery("delete WikiPage wp where wp.title=:title and wp.namespace=:namespace")
                        .setParameter("title", givenTitle.title())
                        .setParameter("namespace", givenTitle.namespace())
                        .executeUpdate()
        );
    }

    @Test
    @DisplayName("트랜잭션 시점에서의 편집 충돌 테스트")
    void conflict_in_transaction_time() throws InterruptedException {
        UUID contributorA = UUID.randomUUID();
        UUID contributorB = UUID.randomUUID();
        WikiPageDataForUpdate editDataA = wikiPageCommandService.proclaimUpdate(contributorA, givenTitle);
        WikiPageDataForUpdate editDataB = wikiPageCommandService.proclaimUpdate(contributorB, givenTitle);

        String contentA = editDataA.content() + randString();
        String contentB = editDataB.content() + randString();

        EditWorker workerA = EditWorker.builder()
                .wikiPageCommandService(wikiPageCommandService)
                .content(contentA)
                .contributorId(contributorA)
                .wikiPageDataForUpdate(editDataA)
                .build();

        EditWorker workerB = EditWorker.builder()
                .wikiPageCommandService(wikiPageCommandService)
                .content(contentB)
                .contributorId(contributorB)
                .wikiPageDataForUpdate(editDataB)
                .build();

        Thread threadA = new Thread(workerA);
        threadA.start();
        Thread threadB = new Thread(workerB);
        threadB.start();

        threadA.join();
        threadB.join();

        // then
        Exception exceptionA = workerA.getException();
        Exception exceptionB = workerB.getException();

        EditWorker successWorker;
        EditWorker failWorker;

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

        WikiPageDataForUpdate found = wikiPageCommandService.proclaimUpdate(UUID.randomUUID(), editDataB.titleNamespaceToWikiPageTitle());
        assertThat(found.content())
                .isNotEqualTo(editDataA.content())
                .isEqualTo(successWorker.getContent())
                .isNotEqualTo(failWorker.getContent())
        ;

        assertThat(failWorker.getException())
                .describedAs("락 확보 실패(데드락 포함)로 인한 예외가 아니어야함.")
                .isNotInstanceOf(CannotAcquireLockException.class)
                .describedAs("유니크 인덱스(page_id, rev_version)가 설정되었기 때문에, Revision 테이블에 insert 가 일어나는 시점에 실패하게 됨.")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Getter
    static class EditWorker implements Runnable {
        private final WikiPageDataForUpdate wikiPageDataForUpdate;
        private final WikiPageCommandService wikiPageCommandService;
        private final UUID contributorId;
        private final String content;
        private Exception exception;

        @Override
        public void run() {
            try {
                wikiPageCommandService.commitUpdate(contributorId, wikiPageDataForUpdate.titleNamespaceToWikiPageTitle(), randString(), wikiPageDataForUpdate.versionToken(), content, new HashSet<>());
            } catch (Exception e) {
                this.exception = e;
            }
        }

        @Builder
        public EditWorker(WikiPageDataForUpdate wikiPageDataForUpdate, WikiPageCommandService wikiPageCommandService, UUID contributorId, String content) {
            this.wikiPageDataForUpdate = wikiPageDataForUpdate;
            this.wikiPageCommandService = wikiPageCommandService;
            this.contributorId = contributorId;
            this.content = content;
        }
    }

}
