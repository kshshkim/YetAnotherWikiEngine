package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aNormalWikiPage;
import static dev.prvt.yawiki.fixture.WikiPageFixture.updateWikiPageRandomly;
import static org.assertj.core.api.Assertions.assertThat;

class WikiPageMapperTest {
    private final WikiPageMapper wikiPageMapper = new WikiPageMapper();

    @Test
    @DisplayName("mapForUpdate - 비활성 위키 페이지 매핑 테스트")
    void mapForUpdate_inactive_wiki_page() {
        // given
        WikiPage wikiPage = aNormalWikiPage();  // 생성 직후이기 때문에 활성 상태가 아님.

        // when
        WikiPageDataForUpdate mapped = wikiPageMapper.mapForUpdate(wikiPage);

        // then
        assertThat(List.of(mapped.title(), mapped.namespace(), mapped.content(), mapped.versionToken()))
                .describedAs("필드에 null 값이 포함되면 안 됨.")
                .doesNotContainNull()
                .describedAs("제목, 네임스페이스, 본문, 버전 토큰이 제대로 매핑됨.")
                .containsExactlyElementsOf(List.of(wikiPage.getTitle(), wikiPage.getNamespace(), wikiPage.getContent(), wikiPage.getVersionToken()));

    }

    @Test
    @DisplayName("mapForUpdate - 활성 위키 페이지 매핑 테스트")
    void mapForUpdate_WikiPage_existing_WikiPage() {
        // given
        WikiPage wikiPage = aNormalWikiPage();
        updateWikiPageRandomly(wikiPage);

        // when
        WikiPageDataForUpdate mapped = wikiPageMapper.mapForUpdate(wikiPage);

        // then
        assertThat(List.of(mapped.title(), mapped.namespace(), mapped.content(), mapped.versionToken()))
                .describedAs("필드에 null 값이 포함되면 안 됨.")
                .doesNotContainNull()
                .describedAs("제목, 네임스페이스, 본문, 버전 토큰이 제대로 매핑됨.")
                .containsExactlyElementsOf(List.of(wikiPage.getTitle(), wikiPage.getNamespace(), wikiPage.getContent(), wikiPage.getVersionToken()));
    }

}