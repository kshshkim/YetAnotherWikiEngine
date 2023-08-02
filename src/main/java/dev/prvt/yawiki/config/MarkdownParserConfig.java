package dev.prvt.yawiki.config;

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
import dev.prvt.yawiki.application.domain.markdownparser.FlexMarkReferenceExtractor;
import dev.prvt.yawiki.application.domain.wikipage.InnerReferenceExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class MarkdownParserConfig {
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
    public InnerReferenceExtractor documentReferenceExtractor() {
        return new FlexMarkReferenceExtractor(flexmarkParser());
    }
}
