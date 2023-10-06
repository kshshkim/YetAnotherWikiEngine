package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.WikiPageDomainService;
import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageReferenceUpdaterException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.ReferencedTitleExtractor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


/**
 * <p>이 테스트 클래스에서 검증해야하는 책임</p>
 * <ul>
 * <li>
 * 트랜잭션이 적절히 구성되었는지
 *     <ul>
 *     <li>
 *         마크다운 파싱 작업이 트랜잭션의 바깥에서 실행되는지
 *     </li>
 *     <li>
 *         업데이트 작업이 트랜잭션 범위 안에서 실행되는지
 *     </li>
 *     </ul>
 * </li>
 * <li>
 * 의존하고 있는 하위 객체에서 문제가 발생했을 때 적절히 처리되는지
 * </li>
 * <li>
 * 의존하고 있는 하위 객체를 적절히 호출하는지
 * </li>
 * </ul>
 * <p>이 목록에 포함되지 않은 사항들은 이 테스트 클래스에서 검증할 사항이 아니며, 별도의 유닛테스트 혹은 통합 테스트에서 다뤄야함.</p>
 * <p>현재 HikariCP 구현체를 이용해서 DB 커넥션 반환을 검증하고 있지만, 테스트를 병렬실행하거나 DataSource 구현체가 바뀐다면 테스트 코드를 수정해야함.</p>
 */

@Slf4j
@SpringBootTest
class WikiPageCommandServiceImplTest {
    private final String PARAMETER_CHECK = "파라미터가 적절히 넘어와야함.";


    private String extractorFailTrigger;
    private String extractorFailMessage;

    private UUID updaterFailTrigger;
    private String updaterFailMessage;

    private UUID givenContributorId;
    private String givenTitle;
    private String givenContent;
    private String givenComment;
    private Set<String> givenReferences;
    private String givenVersionToken;

    private boolean called_ReferencedTitleExtractor_extractReferencedTitles;
    private boolean called_WikiPageDomainService_commitUpdate;
    private boolean called_WikiPageDomainService_updateProclaim;
    private boolean called_WikiPageDomainService_delete;

    class TestWikiPageDomainService extends WikiPageDomainService {
        public TestWikiPageDomainService() {
            super(null, null, null, null, null);
        }

        @Override
        public void commitUpdate(UUID contributorId, String title, String content, String comment, String versionToken, Set<String> references) {
            called_WikiPageDomainService_commitUpdate = true;
            if (updaterFailTrigger.equals(contributorId)) {
                throw new WikiPageReferenceUpdaterException(updaterFailMessage);
            }

            boolean isInTransaction = TransactionSynchronizationManager.isActualTransactionActive();
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

            assertThat(isInTransaction)
                    .describedAs("트랜잭션 중에 일어나야함.")
                    .isTrue();
            assertThat(isReadOnly)
                    .describedAs("읽기 전용이면 안 됨.")
                    .isFalse();

            assertThat(tuple(contributorId, title, content, comment, references))
                    .describedAs(PARAMETER_CHECK)
                    .isEqualTo(tuple(givenContributorId, givenTitle, givenContent, givenComment, givenReferences));
        }

        @Override
        public WikiPage proclaimUpdate(UUID contributorId, String wikiPageTitle) {
            called_WikiPageDomainService_updateProclaim = true;
            return WikiPage.create(wikiPageTitle);
        }

        @Override
        public void delete(UUID contributorId, String title, String comment) {
            called_WikiPageDomainService_delete = true;
        }
    }

    class DummyReferenceTitleExtractor implements ReferencedTitleExtractor {
        @Override
        public Set<String> extractReferencedTitles(String rawMarkDown) {
            called_ReferencedTitleExtractor_extractReferencedTitles = true;

            if (rawMarkDown.equals(extractorFailTrigger)) {
                throw new RuntimeException(extractorFailMessage);
            }


            boolean isInTransaction = TransactionSynchronizationManager.isActualTransactionActive();

            assertThat(isInTransaction)
                    .describedAs("위키 레퍼런스 추출은 트랜잭션 도중에 일어나선 안 됨.")
                    .isFalse();

            assertThat(rawMarkDown)
                    .describedAs(PARAMETER_CHECK)
                    .isEqualTo(givenContent);
            return givenReferences;
        }
    }


    @Autowired
    private PlatformTransactionManager platformTransactionManager;  // platformTransactionManager 만 테스트용 추상화를 못 했음. todo

    private WikiPageCommandServiceImpl wikiPageCommandServiceImpl;

    @BeforeEach
    void beforeEach() {
        // 의존중인 하위 객체들이 예외를 반환하도록 하는 트리거, 예외 발생시 기대하는 메시지
        extractorFailTrigger = UUID.randomUUID().toString();
        extractorFailMessage = UUID.randomUUID().toString();

        updaterFailTrigger = UUID.randomUUID();
        updaterFailMessage = UUID.randomUUID().toString();

        // given 인자들 초기화. 하위 모듈에서 파라미터를 적절하게 넘겨받는지 확인하기 위해 사용됨.
        givenContributorId = UUID.randomUUID();
        givenTitle = UUID.randomUUID().toString();
        givenContent = UUID.randomUUID().toString();
        givenComment = UUID.randomUUID().toString();
        givenReferences = Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        givenVersionToken = UUID.randomUUID().toString();

        // wikiPageCommandService 재생성
        wikiPageCommandServiceImpl = new WikiPageCommandServiceImpl(new DummyReferenceTitleExtractor(), new TestWikiPageDomainService(), platformTransactionManager, new WikiPageMapper());

        // 참조하고 있는 클래스들의 호출이 적절하게 일어났는지 여부
        called_ReferencedTitleExtractor_extractReferencedTitles = false;
        called_WikiPageDomainService_commitUpdate = false;
        called_WikiPageDomainService_updateProclaim = false;
        called_WikiPageDomainService_delete = false;
    }

    @Test
    void commitUpdate_should_success() {
        assertThatCode(() -> wikiPageCommandServiceImpl.commitUpdate(givenContributorId, givenTitle, givenComment, givenVersionToken, givenContent))
                .describedAs("should success")
                .doesNotThrowAnyException();

        assertThat(tuple(called_ReferencedTitleExtractor_extractReferencedTitles, called_WikiPageDomainService_commitUpdate))
                .describedAs("모두 호출되었음.")
                .isEqualTo(tuple(true, true));
    }

    @Test
    void commitUpdate_should_fail_when_updater_fails() {
        givenContributorId = updaterFailTrigger;

        assertThatThrownBy(() -> wikiPageCommandServiceImpl.commitUpdate(givenContributorId, givenTitle, givenComment, givenVersionToken, givenContent))
                .describedAs("업데이터에서만 문제가 터져야함.")
                .hasMessageContaining(updaterFailMessage);

        assertThat(tuple(called_ReferencedTitleExtractor_extractReferencedTitles, called_WikiPageDomainService_commitUpdate))
                .describedAs("모두 호출되었음.")
                .isEqualTo(tuple(true, true));
    }

    @Test
    void commitUpdate_should_fail_when_extractor_fails() {
        givenContent = extractorFailTrigger;

        assertThatThrownBy(() -> wikiPageCommandServiceImpl.commitUpdate(givenContributorId, givenTitle, givenComment, givenVersionToken, givenContent))
                .describedAs("extractor 에서만 문제가 터져야함.")
                .hasMessageContaining(extractorFailMessage);

        assertThat(tuple(called_ReferencedTitleExtractor_extractReferencedTitles, called_WikiPageDomainService_commitUpdate))
                .describedAs("extracting 과정에 문제가 생겨서 업데이터는 호출되지 않았음.")
                .isEqualTo(tuple(true, false));
    }

    @Test
    void proclaimUpdate_should_success() {
        WikiPageDataForUpdate wikiPageDataForUpdate = wikiPageCommandServiceImpl.proclaimUpdate(givenContributorId, givenTitle);

        assertThat(called_WikiPageDomainService_updateProclaim).isTrue();
        assertThat(wikiPageDataForUpdate).isNotNull();
    }

    @Test
    void delete_should_success() {
        wikiPageCommandServiceImpl.delete(givenContributorId, givenTitle, givenComment);
        assertThat(called_WikiPageDomainService_delete).isTrue();
    }
}