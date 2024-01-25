package dev.prvt.yawiki.core.wikititle.existence;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import java.util.Collection;
import org.springframework.stereotype.Component;

@Component
public class WikiPageTitleExistenceCheckerImpl implements WikiPageTitleExistenceChecker {

    /**
     * todo 구현
     * @param toFilter 필터링할 위키 페이지 제목
     * @return
     */
    @Override
    public Collection<WikiPageTitle> filterExistentTitles(Collection<WikiPageTitle> toFilter) {
        return toFilter;
    }
}
