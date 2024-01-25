package dev.prvt.yawiki.web.markdown;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.webutil.converter.WikiPageTitleConverter;
import java.util.Set;
import java.util.stream.Collectors;


public class FlexMarkReferenceExtractor implements ReferencedTitleExtractor {

    private final Parser parser;

    private final WikiReferenceFilter wikiReferenceFilter;

    private final WikiPageTitleConverter wikiPageTitleConverter;

    public FlexMarkReferenceExtractor(
        Parser parser,
        WikiReferenceFilter wikiReferenceFilter,
        WikiPageTitleConverter wikiPageTitleConverter
    ) {
        this.parser = parser;
        this.wikiReferenceFilter = wikiReferenceFilter;
        this.wikiPageTitleConverter = wikiPageTitleConverter;
    }

    private Document parse(String markDown) {  // html 렌더링 시에도 사용됨. 캐시 고려할것.
        return parser.parse(markDown);
    }

    @Override
    public Set<WikiPageTitle> extractReferencedTitles(String rawMarkDown) {
        Document rootNode = parse(rawMarkDown);

        FlexMarkWikiRefCollector collector = new FlexMarkWikiRefCollector();
        NodeVisitor visitor = collector.getNodeVisitor();

        visitor.visit(rootNode);

        return collector.getDistinctPageRefs().stream()
                   .filter(wikiReferenceFilter::isWikiReference)
                   .map(wikiPageTitleConverter::convert)
                   .collect(Collectors.toSet());
    }
}
