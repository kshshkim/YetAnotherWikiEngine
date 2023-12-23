package dev.prvt.yawiki.web.markdown;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.Set;
import java.util.stream.Collectors;


public class FlexMarkReferenceExtractor implements ReferencedTitleExtractor {
    private final Parser parser;

    public FlexMarkReferenceExtractor(Parser parser) {
        this.parser = parser;
    }

    private Document parse(String markDown) {  // html 렌더링 시에도 사용됨. 캐시 고려할것.
        return parser.parse(markDown);
    }

    private WikiPageTitle parseToWikiPageTitle(String title) { // 임시로 NORMAL 반환하게 둠. todo title parser
        return new WikiPageTitle(title, Namespace.NORMAL);
    }

    @Override
    public Set<WikiPageTitle> extractReferencedTitles(String rawMarkDown) {
        Document rootNode = parse(rawMarkDown);

        FlexMarkWikiRefCollector collector = new FlexMarkWikiRefCollector();
        NodeVisitor visitor = collector.getNodeVisitor();

        visitor.visit(rootNode);

        return collector.getDistinctPageRefs().stream()
                .map(this::parseToWikiPageTitle)
                .collect(Collectors.toSet());
    }
}
