package dev.prvt.yawiki.core.wikipage.application;

import com.zaxxer.hikari.HikariDataSource;
import dev.prvt.yawiki.core.wikipage.domain.WikiPageUpdateValidator;
import dev.prvt.yawiki.core.wikipage.domain.WikiPageUpdater;
import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageUpdaterException;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.ReferencedTitleExtractor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
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
 *         편집 이전 검증 작업이 읽기 전용 트랜잭션에서 실행되는지
 *     </li>
 *     <li>
 *         <p>마크다운 파싱 작업 중에 트랜잭션이 활성화되지 않아야하며, DB 커넥션 역시 반환해야함.</p>
 *     </li>
 *     <li>
 *         업데이트 작업이 트랜잭션 범위 안에서 실행되는지
 *     </li>
 *     <li>
 *         트랜잭션이 순차적으로 이루어지는지
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

    private String updateValidatorFailTrigger;
    private String updateValidatorFailMessage;

    private UUID givenContributorId;
    private String givenTitle;
    private String givenContent;
    private String givenComment;
    private Set<String> givenReferences;
    private String givenVersionToken;

    private boolean validatingExecuted;
    private boolean extractingExecuted;
    private boolean updatingExecuted;

    /**
     * HikariDataSource 를 DataSource 구현체로 사용하고, 테스트를 병렬 실행하지 않을 경우에만 올바르게 수행됨.
     */
    void assertDbConnectionIsReleased() {
        if (this.dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;

            int activeConnections = hikariDataSource.getHikariPoolMXBean().getActiveConnections();
            SoftAssertions softly = new SoftAssertions();

            softly.assertThat(activeConnections)
                    .describedAs("DB 커넥션 반환 테스트. 테스트를 병렬 실행할 경우, 다른 테스트 케이스에서 DB 커넥션을 점유하여 테스트가 실패할 수 있음.")
                    .isEqualTo(0);
        } else {
            log.warn("DB Connection 반환이 적절하게 테스트되지 않았음. 지원하지 않는 DataSource 구현체 {}", dataSource.getClass());
        }
    }

    class TestWikiPageUpdater extends WikiPageUpdater {
        public TestWikiPageUpdater() {
            super(null, null);
        }

        @Override
        public void update(UUID contributorId, String title, String content, String comment, Set<String> references) {
//            super.update(contributorId, title, content, comment, references);
            updatingExecuted = true;
            if (updaterFailTrigger.equals(contributorId)) {
                throw new WikiPageUpdaterException(updaterFailMessage);
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
    }

    class DummyReferenceTitleExtractor implements ReferencedTitleExtractor {
        @Override
        public Set<String> extractReferencedTitles(String rawMarkDown) {
            extractingExecuted = true;

            if (rawMarkDown.equals(extractorFailTrigger)) {
                throw new RuntimeException(extractorFailMessage);
            }


            boolean isInTransaction = TransactionSynchronizationManager.isActualTransactionActive();

            assertDbConnectionIsReleased();

            assertThat(isInTransaction)
                    .describedAs("위키 레퍼런스 추출은 트랜잭션 도중에 일어나선 안 됨.")
                    .isFalse();

            assertThat(rawMarkDown)
                    .describedAs(PARAMETER_CHECK)
                    .isEqualTo(givenContent);
            return givenReferences;
        }
    }

    class TestWikiPageUpdateValidator extends WikiPageUpdateValidator {
        public TestWikiPageUpdateValidator() {
            super(null, null, null);
        }

        @SneakyThrows
        @Override
        public void validate(UUID actorId, String wikiPageTitle, String versionToken) {
//            super.validate(actorId, wikiPageTitle, versionToken);

            validatingExecuted = true;

            if (versionToken.equals(updateValidatorFailTrigger)) {
                throw new RuntimeException(updateValidatorFailMessage);
            }

            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

            assertThat(isReadOnly)
                    .describedAs("읽기 전용 트랜잭션에서 일어나야함.")
                    .isTrue();

            assertThat(tuple(actorId, wikiPageTitle, versionToken))
                    .describedAs(PARAMETER_CHECK)
                    .isEqualTo(tuple(givenContributorId, givenTitle, givenVersionToken));
        }
    }

    @Autowired
    private PlatformTransactionManager platformTransactionManager;  // platformTransactionManager 만 테스트용 추상화를 못 했음. todo

    @Autowired
    private DataSource dataSource;

    private WikiPageCommandServiceImpl wikiPageCommandServiceImpl;

    @BeforeEach
    void beforeEach() {
        // 의존중인 하위 객체들이 예외를 반환하도록 하는 트리거, 예외 발생시 기대하는 메시지
        extractorFailTrigger = UUID.randomUUID().toString();
        extractorFailMessage = UUID.randomUUID().toString();

        updaterFailTrigger = UUID.randomUUID();
        updaterFailMessage = UUID.randomUUID().toString();

        updateValidatorFailTrigger = UUID.randomUUID().toString();
        updateValidatorFailMessage = UUID.randomUUID().toString();

        // given 인자들 초기화. 하위 모듈에서 파라미터를 적절하게 넘겨받는지 확인하기 위해 사용됨.
        givenContributorId = UUID.randomUUID();
        givenTitle = UUID.randomUUID().toString();
        givenContent = UUID.randomUUID().toString();
        givenComment = UUID.randomUUID().toString();
        givenReferences = Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        givenVersionToken = UUID.randomUUID().toString();

        // wikiPageCommandService 재생성
        wikiPageCommandServiceImpl = new WikiPageCommandServiceImpl(new TestWikiPageUpdateValidator(), new DummyReferenceTitleExtractor(), new TestWikiPageUpdater(), platformTransactionManager);

        // 마크다운 파싱이 일어났는지 여부
        validatingExecuted = false;
        extractingExecuted = false;
        updatingExecuted = false;
    }

    @Test
    void should_success() {
        assertThatCode(() -> wikiPageCommandServiceImpl.updateWikiPage(givenContributorId, givenTitle, givenComment, givenVersionToken, givenContent))
                .describedAs("should success")
                .doesNotThrowAnyException();

        assertThat(tuple(validatingExecuted, extractingExecuted, updatingExecuted))
                .describedAs("모두 호출되었음.")
                .isEqualTo(tuple(true, true, true));
    }

    @Test
    void should_fail_when_updater_fails() {
        givenContributorId = updaterFailTrigger;

        assertThatThrownBy(() -> wikiPageCommandServiceImpl.updateWikiPage(givenContributorId, givenTitle, givenComment, givenVersionToken, givenContent))
                .describedAs("업데이터에서만 문제가 터져야함.")
                .hasMessageContaining(updaterFailMessage);

        assertThat(tuple(validatingExecuted, extractingExecuted, updatingExecuted))
                .describedAs("모두 호출되었음.")
                .isEqualTo(tuple(true, true, true));
    }

    @Test
    void should_fail_when_extractor_fails() {
        givenContent = extractorFailTrigger;

        assertThatThrownBy(() -> wikiPageCommandServiceImpl.updateWikiPage(givenContributorId, givenTitle, givenComment, givenVersionToken, givenContent))
                .describedAs("extractor 에서만 문제가 터져야함.")
                .hasMessageContaining(extractorFailMessage);

        assertThat(tuple(validatingExecuted, extractingExecuted, updatingExecuted))
                .describedAs("extracting 과정에 문제가 생겨서 업데이터는 호출되지 않았음.")
                .isEqualTo(tuple(true, true, false));
    }

    @Test
    void should_fail_when_update_validator_fails() {
        givenVersionToken = updateValidatorFailTrigger;

        assertThatThrownBy(() -> wikiPageCommandServiceImpl.updateWikiPage(givenContributorId, givenTitle, givenComment, givenVersionToken, givenContent))
                .describedAs("validator 에서만 문제가 터져야함.")
                .hasMessageContaining(updateValidatorFailMessage);

        assertThat(extractingExecuted)
                .describedAs("검증이 실패한 경우 레퍼런스 extraction 이 일어나서는 안 됨.")
                .isFalse();

        assertThat(tuple(validatingExecuted, extractingExecuted, updatingExecuted))
                .describedAs("validator 에서 문제가 생기면 다음 단계로 넘어가지 않고 바로 중단되어야함.")
                .isEqualTo(tuple(true, false, false));
    }
}