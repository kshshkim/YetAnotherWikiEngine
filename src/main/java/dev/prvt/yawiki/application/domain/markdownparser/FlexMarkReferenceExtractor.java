package dev.prvt.yawiki.application.domain.markdownparser;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import dev.prvt.yawiki.application.domain.wikipage.InnerReferenceExtractor;

import java.util.Set;


public class FlexMarkReferenceExtractor implements InnerReferenceExtractor {
    private final Parser parser;

    public FlexMarkReferenceExtractor(Parser parser) {
        this.parser = parser;
    }

    private Document parse(String markDown) {  // html 렌더링 시에도 사용됨. 캐시 고려할것.
        return parser.parse(markDown);
    }

    @Override
    public Set<String> extractReferencedTitles(String rawMarkDown) {
        Document rootNode = parse(rawMarkDown);

        FlexMarkWikiRefCollector collector = new FlexMarkWikiRefCollector();
        NodeVisitor visitor = collector.getNodeVisitor();

        visitor.visit(rootNode);

        return collector.getDistinctPageRefs();
    }
}
