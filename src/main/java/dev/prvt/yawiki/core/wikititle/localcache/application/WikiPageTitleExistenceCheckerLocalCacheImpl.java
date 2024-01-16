package dev.prvt.yawiki.core.wikititle.localcache.application;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.existence.WikiPageTitleExistenceChecker;
import dev.prvt.yawiki.core.wikititle.localcache.domain.LocalCacheStorage;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WikiPageTitleExistenceCheckerLocalCacheImpl implements WikiPageTitleExistenceChecker {
    private final LocalCacheStorage localCacheStorage;

    @Override
    public Collection<WikiPageTitle> filterExistentTitles(Collection<WikiPageTitle> toFilter) {
        return localCacheStorage.filterExistentTitles(toFilter);
    }

}
