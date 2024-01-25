package dev.prvt.yawiki.titleexistence.cache.application;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.titleexistence.cache.domain.CacheStorage;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WikiPageTitleExistenceFilter {
    private final CacheStorage cacheStorage;

    public Collection<WikiPageTitle> getNonExistentTitles(Collection<WikiPageTitle> toFilter) {
        return cacheStorage.filterExistentTitles(toFilter);
    }

    public Collection<WikiPageTitle> getNonExistentTitles(Stream<WikiPageTitle> toFilter) {
        return cacheStorage.filterExistentTitles(toFilter);
    }

}
