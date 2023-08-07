package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.prvt.yawiki.Fixture.randString;
import static dev.prvt.yawiki.Fixture.updateWikiPageRandomly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class WikiPageMapperTest {
    private WikiPageMapper wikiPageMapper = new WikiPageMapper();
    @Test
    void mapFrom_new_WikiPage() {
        WikiPage wikiPage = WikiPage.create(randString());
        WikiPageDataForUpdate mapped = wikiPageMapper.mapFrom(wikiPage);

        assertThat(tuple(mapped.title(), mapped.content(), mapped.versionToken()))
                .isEqualTo(tuple(wikiPage.getTitle(), wikiPage.getContent(), wikiPage.getVersionToken()));

        assertThat(List.of(mapped.title(), mapped.content(), mapped.versionToken()))
                .doesNotContainNull();

        assertThat(List.of(mapped.title(), mapped.versionToken()))
                .doesNotContain("");
    }

    @Test
    void mapFrom_existing_WikiPage() {
        WikiPage wikiPage = WikiPage.create(randString());
        updateWikiPageRandomly(wikiPage);
        WikiPageDataForUpdate mapped = wikiPageMapper.mapFrom(wikiPage);

        assertThat(tuple(mapped.title(), mapped.content(), mapped.versionToken()))
                .isEqualTo(tuple(wikiPage.getTitle(), wikiPage.getContent(), wikiPage.getVersionToken()));

        assertThat(List.of(mapped.title(), mapped.content(), mapped.versionToken()))
                .doesNotContainNull()
                .doesNotContain("");
    }
}