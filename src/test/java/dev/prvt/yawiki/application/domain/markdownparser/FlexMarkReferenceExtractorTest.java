package dev.prvt.yawiki.application.domain.markdownparser;

import com.vladsch.flexmark.parser.Parser;
import dev.prvt.yawiki.config.MarkdownParserConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FlexMarkReferenceExtractorTest {
    MarkdownParserConfig conf = new MarkdownParserConfig();
    Parser parser = conf.flexmarkParser();
    FlexMarkReferenceExtractor extractor = new FlexMarkReferenceExtractor(parser);

    @Test
    void extractReferencedTitles() {
        // given
        String sample = "[공식 사이트](https://daringfireball.net/projects/markdown/) \n" +
                        "마크다운 (Markdown)은 [[마크업 언어]]의 일종으로, 존 그루버(John Gruber)와 아론 스워츠(Aaron Swartz)가 만들었다. " +
                        "온갖 태그로 범벅된 [[HTML]] 문서 등과 달리, 읽기도 쓰기도 쉬운 문서 양식을 지향한다. " +
                        "그루버는 [[MarkDown|마크다운]]으로 작성한 문서를 [[HTML]]로 변환하는 [[펄|Perl]] 스크립트도 만들었다. " +
                        "흔히 볼 수 있는 문서(파일명)은 \"README.md\", [[화일|파일]]의 [[확장자]]는 .md 또는 .markdown을 쓴다.";
        Set<String> givenRefs = Set.of("마크업 언어", "HTML", "마크다운", "Perl", "파일", "확장자");

        // when
        Set<String> extracted = extractor.extractReferencedTitles(sample);

        // then
        assertThat(extracted)
                .containsExactlyInAnyOrderElementsOf(givenRefs);

    }
}