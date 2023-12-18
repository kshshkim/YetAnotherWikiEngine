package dev.prvt.yawiki.core.wikititle.localcache.infra.updater;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleHistory;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleUpdateType;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangeLog;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.RemoteChangesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RemoteChangesRepositoryImplTest {

    @Autowired
    EntityManager em;

    RemoteChangesRepository remoteChangesRepository;

    LocalDateTime now;

    @BeforeEach
    void init() {
        remoteChangesRepository = new RemoteChangesRepositoryImpl(em);

        now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        LocalDateTime twoMinutesAgo = now.minusMinutes(2);
        LocalDateTime threeMinutesAgo = now.minusMinutes(3);
        WikiPageTitle titleOne = aWikiPageTitle();
        WikiPageTitle titleTwo = aWikiPageTitle();
        WikiPageTitle titleThree = aWikiPageTitle();


        TitleHistory first = TitleHistory.builder()
                .pageTitle(titleOne.title())
                .namespace(titleOne.namespace())
                .createdAt(threeMinutesAgo)
                .titleUpdateType(TitleUpdateType.CREATED)
                .build();

        TitleHistory second = TitleHistory.builder()
                .pageTitle(titleTwo.title())
                .namespace(titleTwo.namespace())
                .createdAt(twoMinutesAgo)
                .titleUpdateType(TitleUpdateType.CREATED)
                .build();

        TitleHistory third = TitleHistory.builder()
                .pageTitle(titleThree.title())
                .namespace(titleThree.namespace())
                .createdAt(oneMinuteAgo)
                .titleUpdateType(TitleUpdateType.CREATED)
                .build();

        em.persist(first);
        em.persist(second);
        em.persist(third);
        em.flush();
        em.clear();
    }

    @Test
    void findRemoteChangesByCursor() {
        List<RemoteChangeLog> all = remoteChangesRepository.findRemoteChangesByCursor(now.minusMinutes(4), now);

        assertThat(all)
                .describedAs("모두 찾아옴")
                .hasSize(3);

        List<RemoteChangeLog> two = remoteChangesRepository.findRemoteChangesByCursor(now.minusMinutes(3), now);

        assertThat(two)
                .describedAs("exclusive 조건이기 때문에 2개만 찾아와야함.")
                .hasSize(2);

        List<RemoteChangeLog> none = remoteChangesRepository.findRemoteChangesByCursor(now.plusMinutes(3), LocalDateTime.MAX);

        assertThat(none)
                .describedAs("결과가 없어야함.")
                .isEmpty();
    }
    @Test
    void findRemoteChangesByCursor_projection() {
        List<RemoteChangeLog> all = remoteChangesRepository.findRemoteChangesByCursor(now.minusMinutes(4), now);

        assertThat(all)
                .describedAs("모두 찾아옴")
                .hasSize(3);

        for (RemoteChangeLog remoteChangeLog : all) {
            assertThat(remoteChangeLog.title()).isNotNull();
            assertThat(remoteChangeLog.changeType()).isNotNull();
            assertThat(remoteChangeLog.timestamp()).isNotNull();
        }
    }
    @Test
    void findRemoteChangesByCursor_order_by() {
        List<RemoteChangeLog> actual = remoteChangesRepository.findRemoteChangesByCursor(now.minusMinutes(4), now);

        assertThat(actual)
                .describedAs("모두 찾아옴")
                .hasSize(3);

        List<RemoteChangeLog> expected = actual.stream()
                .sorted(Comparator.comparing(RemoteChangeLog::timestamp))
                .toList();

        assertThat(actual)
                .containsExactlyElementsOf(actual);
    }
}