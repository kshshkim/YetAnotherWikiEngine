package dev.prvt.yawiki.web.api.v1.title;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.existence.WikiPageTitleExistenceChecker;
import dev.prvt.yawiki.web.api.v1.title.response.NonExistentTitleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/title")
@RequiredArgsConstructor
public class TitleController {

    private final WikiPageTitleExistenceChecker titleExistenceChecker;

    @GetMapping
    public NonExistentTitleResponse getNonExistTitles(
            @RequestParam Collection<WikiPageTitle> titles
    ) {
        return NonExistentTitleResponse.from(
                titleExistenceChecker.filterExistingTitles(titles)
        );
    }
}
