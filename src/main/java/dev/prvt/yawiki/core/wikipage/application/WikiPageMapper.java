package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.springframework.stereotype.Component;

@Component
public class WikiPageMapper {
    public WikiPageDataForUpdate mapFrom(WikiPage wikiPage) {
        return new WikiPageDataForUpdate(wikiPage.getTitle(), wikiPage.getContent(), wikiPage.getVersionToken());
    }
}
