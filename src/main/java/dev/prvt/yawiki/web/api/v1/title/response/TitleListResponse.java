package dev.prvt.yawiki.web.api.v1.title.response;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.Collection;

public record TitleListResponse(
        int size,
        Collection<WikiPageTitle> titles
) {
    public static TitleListResponse from(Collection<WikiPageTitle> nonExistentTitles) {
        return new TitleListResponse(nonExistentTitles.size(), nonExistentTitles);
    }
}
