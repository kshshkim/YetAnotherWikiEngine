package dev.prvt.yawiki.core.wikititle.history.application;

import dev.prvt.yawiki.common.model.TitleUpdateType;
import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;

import static dev.prvt.yawiki.common.testutil.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class TitleHistoryServiceTest {

    @Autowired
    TitleHistoryService titleHistoryService;

    @Autowired
    EntityManager em;

    @Test
    void append() {
        // given
        WikiPageTitle givenWikiPageTitle = new WikiPageTitle(randString(), Namespace.NORMAL);
        LocalDateTime givenTimestamp = LocalDateTime.now();

        // when
        titleHistoryService.append(givenWikiPageTitle, TitleUpdateType.CREATED, givenTimestamp);

        // then
        String queryString = "select th from TitleHistory th where th.namespace=:namespace and th.pageTitle=:title";
        TitleHistory found = em.createQuery(
                        queryString,
                        TitleHistory.class
                )
                .setParameter("namespace", givenWikiPageTitle.namespace())
                .setParameter("title", givenWikiPageTitle.title())
                .getSingleResult();

        assertThat(found)
                .isNotNull();

        assertThat(found.getWikiPageTitle())
                .isEqualTo(givenWikiPageTitle);

        assertThat(found.getTitleUpdateType())
                .isEqualTo(TitleUpdateType.CREATED);
    }
}