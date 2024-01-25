package dev.prvt.yawiki.web.markdown;

import com.vladsch.flexmark.ext.wikilink.WikiLink;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.HashSet;
import java.util.Set;

public class FlexMarkWikiRefCollector implements Visitor<WikiLink> {
    private final Set<String> distinctPageRefs = new HashSet<>();
    public Set<String> getDistinctPageRefs() {
        return distinctPageRefs;
    }
    public NodeVisitor getNodeVisitor() {
        return new NodeVisitor(new VisitHandler<>(WikiLink.class, this));
    }

    @Override
    public void visit(WikiLink node) {
        BasedSequence pageRef = node.getPageRef();
        if (!pageRef.equals(BasedSequence.NULL)) {
            distinctPageRefs.add(pageRef.toString());
        }
    }
}
