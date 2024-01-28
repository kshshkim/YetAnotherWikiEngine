package dev.prvt.yawiki.web.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.util.NamespaceParser;
import dev.prvt.yawiki.common.util.web.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.common.util.web.converter.WikiPageTitleConverterImpl;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
class ReferencedTitleExtractorTest {

    Map<String, Namespace> namespaceMap = Map.of(
        "틀", Namespace.TEMPLATE,
        "파일", Namespace.FILE,
        "분류", Namespace.CATEGORY
    );
    WikiPageTitleConverter wikiPageTitleConverter = new WikiPageTitleConverterImpl(new NamespaceParser(namespaceMap));
    ReferencedTitleExtractor extractor = new ReferencedTitleRegexExtractor(wikiPageTitleConverter);

    @Test
    void extractReferencedTitles() {
        // given
        String sample = """
            [[https://daringfireball.net/projects/markdown/|공식 사이트]]
            
            마크다운 (Markdown)은 [[마크업 언어]]의 일종으로, 존 그루버(John Gruber)와 아론 스워츠(Aaron Swartz)가 만들었다.
            온갖 태그로 범벅된 [[HTML]] 문서 등과 달리, 읽기도 쓰기도 쉬운 문서 양식을 지향한다.
            그루버는 [[마크다운|MarkDown]]으로 작성한 문서를 [[HTML]]로 변환하는 [[Perl|펄]] 스크립트도 만들었다.
            흔히 볼 수 있는 문서(파일명)은 "README.md", [[파일|화일]]의 [[확장자]]는 .md 또는 .markdown을 쓴다.
            
            [[]]
            [[ ]]
            [[  ]]
            
            [[분류:마크다운]]
            """;

        Map<String, WikiPageTitle> sampleReferences = Map.of(
            "마크다운", new WikiPageTitle("마크다운", Namespace.NORMAL),
            "마크업 언어", new WikiPageTitle("마크업 언어", Namespace.NORMAL),
            "html", new WikiPageTitle("HTML", Namespace.NORMAL),
            "Perl", new WikiPageTitle("Perl", Namespace.NORMAL),
            "파일", new WikiPageTitle("파일", Namespace.NORMAL),
            "확장자", new WikiPageTitle("확장자", Namespace.NORMAL),
            "분류:마크다운", new WikiPageTitle("마크다운", Namespace.CATEGORY)
        );

        // when
        Set<WikiPageTitle> extracted = extractor.extractReferencedTitles(sample);
        log.info("extracted = {}", extracted);

        // then
        WikiPageTitle blank = new WikiPageTitle("", Namespace.NORMAL);
        WikiPageTitle httpLink = new WikiPageTitle("https://daringfireball.net/projects/markdown/", Namespace.NORMAL);

        assertThat(extracted)
            .describedAs("[[실제 문서 제목|링크 제목]] 형태 파싱시 실제 문서 제목을 추출해야함.")
            .contains(sampleReferences.get("마크다운"))
            .describedAs("분류를 적절히 파싱해야함.")
            .contains(sampleReferences.get("분류:마크다운"))
            .describedAs("빈 제목을 추출해서는 안 됨.")
            .doesNotContain(blank)
            .describedAs("외부 링크를 추출해서는 안 됨.")
            .doesNotContain(httpLink)
        ;

        assertThat(extracted)
            .describedAs("레퍼런스가 누락되거나 다른 값이 들어가서는 안 됨.")
            .containsExactlyInAnyOrderElementsOf(sampleReferences.values());
    }

    @Test
    void testEscape() {
        assertThat(extractor.extractReferencedTitles("[[|]]"))
            .describedAs("유효하지 않은 레퍼런스이기 때문에 비어있어야함.")
            .isEmpty();

        assertThat(extractor.extractReferencedTitles("[[\\|]]"))
            .describedAs("유효한 레퍼런스이기 때문에 적절한 레퍼런스를 포함해야함.")
            .containsExactly(new WikiPageTitle("|", Namespace.NORMAL));

        assertThat(extractor.extractReferencedTitles("[[여\\|러\\|개|보여지는 제목]]"))
            .containsExactly(new WikiPageTitle("여|러|개", Namespace.NORMAL));

        assertThat(extractor.extractReferencedTitles("[[\\\\]]"))
            .containsExactly(new WikiPageTitle("\\", Namespace.NORMAL));

        assertThat(extractor.extractReferencedTitles("[[\\#]]"))
            .containsExactly(new WikiPageTitle("#", Namespace.NORMAL));

        assertThat(extractor.extractReferencedTitles("[[##]]"))
            .containsExactly(new WikiPageTitle("#", Namespace.NORMAL));

        assertThat(extractor.extractReferencedTitles("[[\\#1 To Infinity]]"))
            .containsExactly(new WikiPageTitle("#1 To Infinity", Namespace.NORMAL));

        assertThat(extractor.extractReferencedTitles("[[[제목\\]]]"))
            .containsExactly(new WikiPageTitle("[제목]", Namespace.NORMAL));

        assertThat(extractor.extractReferencedTitles("[[\\[제목\\]]]"))
            .containsExactly(new WikiPageTitle("[제목]", Namespace.NORMAL));

        assertThat(extractor.extractReferencedTitles("[[제목]"))
            .describedAs("브라켓이 제대로 닫히지 않은 레퍼런스는 유효하지 않음.")
            .isEmpty();

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