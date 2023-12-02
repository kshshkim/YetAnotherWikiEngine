package dev.prvt.yawiki.core.wikititle.history.domain;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class TitleHistoryTest {

    private final TitleHistoryFactory titleHistoryFactory = new TitleHistoryFactory();

    WikiPageTitle givenTitle;
    TitleHistory givenTitleHistory;

    @BeforeEach
    void init() {
        givenTitle = new WikiPageTitle(randString(), Namespace.NORMAL);
        givenTitleHistory = titleHistoryFactory.create(
                givenTitle,
                TitleUpdateType.CREATED,
                LocalDateTime.now()
        );
    }

    @Test
    void getWikiPageTitle_do_not_create_new_object() {
        WikiPageTitle first = givenTitleHistory.getWikiPageTitle();
        WikiPageTitle second = givenTitleHistory.getWikiPageTitle();

        assertThat(first).isSameAs(second);
    }
}