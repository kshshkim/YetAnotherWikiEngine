package dev.prvt.yawiki.core.wikititle.localcache.application;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.existence.WikiPageTitleExistenceChecker;
import dev.prvt.yawiki.core.wikititle.localcache.domain.CacheStorage;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WikiPageTitleExistenceCheckerImpl implements WikiPageTitleExistenceChecker {
    private final CacheStorage cacheStorage;

    @Override
    public Collection<WikiPageTitle> filterExistentTitles(Collection<WikiPageTitle> toFilter) {
        return cacheStorage.filterExistentTitles(toFilter);
    }

}
