package dev.prvt.yawiki.common.util;

import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StringToWikiPageTitleConverterImplTest {

    @Mock
    NamespaceParser mockNamespaceParser;

    @InjectMocks
    StringToWikiPageTitleConverter wikiPageTitleConverter;

    @Test
    void convert() {
        // given
        String givenTitle = "파일: file.png";

        given(mockNamespaceParser.getNamespace(givenTitle))
                .willReturn(Namespace.FILE);

        // when
        WikiPageTitle result = wikiPageTitleConverter.convert(givenTitle);

        // then
        assertThat(result)
                .isEqualTo(new WikiPageTitle("file.png", Namespace.FILE));
    }

    @Test
    void convert_without_whitespace() {
        // given
        String givenTitle = "파일:file.png";

        given(mockNamespaceParser.getNamespace(givenTitle))
                .willReturn(Namespace.FILE);

        // when
        WikiPageTitle result = wikiPageTitleConverter.convert(givenTitle);

        // then
        assertThat(result)
                .isEqualTo(new WikiPageTitle("file.png", Namespace.FILE));
    }

    @Test
    void convert_NORMAL() {
        // given
        String givenTitle = Namespace.NORMAL.name() + ":제목";
        given(mockNamespaceParser.getNamespace(givenTitle))
                .willReturn(Namespace.NORMAL);

        // when
        WikiPageTitle result = wikiPageTitleConverter.convert(givenTitle);

        // then
        assertThat(result)
                .describedAs("기본 네임스페이스는 구분자가 존재하지 않음.")
                .isEqualTo(new WikiPageTitle(givenTitle, Namespace.NORMAL));
    }
}