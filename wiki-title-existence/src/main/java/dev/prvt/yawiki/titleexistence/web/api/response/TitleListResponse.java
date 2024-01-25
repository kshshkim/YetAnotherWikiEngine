package dev.prvt.yawiki.titleexistence.web.api.response;

import dev.prvt.yawiki.common.model.WikiPageTitle;

import java.util.Collection;

public record TitleListResponse(
        int size,
        Collection<WikiPageTitle> titles
) {
    public static TitleListResponse from(Collection<WikiPageTitle> nonExistentTitles) {
        return new TitleListResponse(nonExistentTitles.size(), nonExistentTitles);
    }
}
