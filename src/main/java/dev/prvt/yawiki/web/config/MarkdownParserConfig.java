package dev.prvt.yawiki.web.config;

import com.vladsch.flexmark.ext.admonition.AdmonitionExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.gfm.users.GfmUsersExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import dev.prvt.yawiki.web.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.web.markdown.FlexMarkReferenceExtractor;
import dev.prvt.yawiki.web.markdown.HttpLinkFilter;
import dev.prvt.yawiki.web.markdown.ReferencedTitleExtractor;
import dev.prvt.yawiki.web.markdown.WikiReferenceFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class MarkdownParserConfig {

    private final WikiPageTitleConverter wikiPageTitleConverter;
    @Bean
    public Parser flexmarkParser() {
        MutableDataSet options = new MutableDataSet();
        options
                .set(Parser.BLANK_LINES_IN_AST, true)
                .set(Parser.EXTENSIONS, Arrays.asList(
                                AdmonitionExtension.create(),  // todo 뭔지 알아볼것
                                FootnoteExtension.create(),  // todo 문서 읽어볼것
                                GfmUsersExtension.create(),  // github flavored user reference. additional configuration required
                                StrikethroughExtension.create(),  // github flavored
                                TaskListExtension.create(),  // github flavored
                                TablesExtension.create(),  //
                                TypographicExtension.create(),
                                WikiLinkExtension.create()
                        )
                )
                .set(WikiLinkExtension.LINK_PREFIX, "w/")
                .set(WikiLinkExtension.LINK_ESCAPE_CHARS, " +/<>")
                .set(WikiLinkExtension.LINK_REPLACE_CHARS, " ----")
                .set(WikiLinkExtension.LINK_FIRST_SYNTAX, true)
                .set(HtmlRenderer.SOFT_BREAK, "<br />\n")
        ;
        return Parser.builder(options).build();
    }

    @Bean
    public WikiReferenceFilter wikiReferenceFilter() {
        return new HttpLinkFilter();
    }

    @Bean
    public ReferencedTitleExtractor documentReferenceExtractor() {
        return new FlexMarkReferenceExtractor(flexmarkParser(), wikiReferenceFilter(), wikiPageTitleConverter);
    }
}
