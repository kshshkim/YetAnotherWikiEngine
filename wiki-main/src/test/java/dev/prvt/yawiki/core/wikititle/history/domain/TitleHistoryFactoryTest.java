package dev.prvt.yawiki.core.wikititle.history.domain;

import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.TitleUpdateType;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static dev.prvt.yawiki.common.testutil.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class TitleHistoryFactoryTest {
    private final TitleHistoryFactory factory = new TitleHistoryFactory();
    @Test
    void create() {
        // given
        LocalDateTime givenTime = LocalDateTime.now();
        TitleUpdateType givenType = TitleUpdateType.CREATED;
        WikiPageTitle givenTitle = new WikiPageTitle(randString(), Namespace.NORMAL);

        // when
        TitleHistory titleHistory = factory.create(givenTitle, givenType, givenTime);

        // then
        assertThat(titleHistory.getCreatedAt())
                .isEqualTo(givenTime);
        assertThat(titleHistory.getWikiPageTitle())
                .isEqualTo(givenTitle);
        assertThat(titleHistory.getTitleUpdateType())
                .isEqualTo(givenType);
    }
}