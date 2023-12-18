package dev.prvt.yawiki.core.wikititle.localcache.application;

import dev.prvt.yawiki.core.wikititle.existence.WikiPageTitleExistenceChecker;
import dev.prvt.yawiki.core.wikititle.localcache.domain.LocalCacheStorage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class WikiPageTitleExistenceCheckerLocalCacheImpl implements WikiPageTitleExistenceChecker {
    private final LocalCacheStorage localCacheStorage;

    @Override
    public Collection<WikiPageTitle> filterExistingTitles(Collection<WikiPageTitle> toFilter) {
        return filterExistingTitles(toFilter.stream())
                .toList();
    }

    @Override
    public Stream<WikiPageTitle> filterExistingTitles(Stream<WikiPageTitle> toFilter) {
        return toFilter
                .filter(title -> !localCacheStorage.exists(title));
    }
}
