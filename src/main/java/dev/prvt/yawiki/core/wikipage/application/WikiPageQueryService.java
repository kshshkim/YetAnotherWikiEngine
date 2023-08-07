package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;

import java.util.List;

public interface WikiPageQueryService {
    WikiPage getDocument(String title);
    List<String> getBackLinks(String title);
    List<Revision> getRevisionHistory(String title, int page, int size);
}
