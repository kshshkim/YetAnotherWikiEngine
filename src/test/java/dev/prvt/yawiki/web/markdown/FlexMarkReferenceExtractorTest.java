package dev.prvt.yawiki.web.markdown;

import com.vladsch.flexmark.parser.Parser;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.web.config.MarkdownParserConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
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
                        "그루버는 [[마크다운|MarkDown]]으로 작성한 문서를 [[HTML]]로 변환하는 [[Perl|펄]] 스크립트도 만들었다. " +
                        "흔히 볼 수 있는 문서(파일명)은 \"README.md\", [[파일|화일]]의 [[확장자]]는 .md 또는 .markdown을 쓴다. [[]]";
        Set<String> givenRefs = Set.of("마크업 언어", "HTML", "마크다운", "Perl", "파일", "확장자");

        // when
        Set<WikiPageTitle> extracted = extractor.extractReferencedTitles(sample);

        // then
        assertThat(extracted.stream().map(WikiPageTitle::title))
                .containsExactlyInAnyOrderElementsOf(givenRefs);

    }

    @Test
    @Disabled
    void extractingBenchmark() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/test/resources/namu_raw_2022년_230802.txt"));
        StringBuilder stringBuilder = new StringBuilder();

        while (true) {
            String read = bufferedReader.readLine();
            if (read == null) {
                break;
            }
            stringBuilder.append(bufferedReader.readLine());
            stringBuilder.append("\n");
        }
        String sample = stringBuilder.toString();

        long start = System.currentTimeMillis();
        Set<WikiPageTitle> titles = extractor.extractReferencedTitles(sample);
        long end = System.currentTimeMillis();
        log.info("finished in {}ms", end - start);
        int size = titles.size();
        log.info("total links = {}", size);
    }
}