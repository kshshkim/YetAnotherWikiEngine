package dev.prvt.yawiki.core.wikipage.infra.converter;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.infra.converter.NamespaceParser;
import dev.prvt.yawiki.core.wikipage.infra.converter.WikiPageTitleConverterImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WikiPageTitleConverterImplTest {

    @Mock
    NamespaceParser mockNamespaceParser;

    @InjectMocks
    WikiPageTitleConverterImpl wikiPageTitleConverter;

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
}