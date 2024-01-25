package dev.prvt.yawiki.titleexistence.cache.application;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.titleexistence.cache.domain.CacheStorage;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WikiPageTitleExistenceFilter {
    private final CacheStorage cacheStorage;

    public Collection<WikiPageTitle> filterExistentTitles(Collection<WikiPageTitle> toFilter) {
        return cacheStorage.filterExistentTitles(toFilter);
    }

}
