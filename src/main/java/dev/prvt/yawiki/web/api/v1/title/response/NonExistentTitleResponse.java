package dev.prvt.yawiki.web.api.v1.title.response;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.Collection;

public record NonExistentTitleResponse(
        int size,
        Collection<WikiPageTitle> nonExistentTitles
) {
    public static NonExistentTitleResponse from(Collection<WikiPageTitle> nonExistentTitles) {
        return new NonExistentTitleResponse(nonExistentTitles.size(), nonExistentTitles);
    }
}
