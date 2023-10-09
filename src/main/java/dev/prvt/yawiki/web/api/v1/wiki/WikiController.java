package dev.prvt.yawiki.web.api.v1.wiki;

import dev.prvt.yawiki.core.wikipage.application.WikiPageCommandService;
import dev.prvt.yawiki.core.wikipage.application.WikiPageQueryService;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfo;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/wiki/*")
@RequiredArgsConstructor
public class WikiController {
    private final WikiPageCommandService wikiPageCommandService;
    private final WikiPageQueryService wikiPageQueryService;

    @GetMapping("/{title}")
    public WikiPageDataForRead getWikiPage(@PathVariable String title) {
        return wikiPageQueryService.getWikiPage(title);
    }

    @GetMapping("/{title}/history")
    public Page<RevisionData> getWikiHistory(
            @PathVariable String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return wikiPageQueryService.getRevisionHistory(title, Pageable.ofSize(size).withPage(page));
    }

    @GetMapping("/{title}/edit")
    public WikiPageDataForUpdate proclaimEdit(
            @PathVariable String title,
            @ContributorInfo ContributorInfoArg contributorInfo
    ) {
        try {
            return wikiPageCommandService.proclaimUpdate(contributorInfo.contributorId(), title);
        } catch (NoSuchWikiPageException e) {
            wikiPageCommandService.create(contributorInfo.contributorId(), title);
            return wikiPageCommandService.proclaimUpdate(contributorInfo.contributorId(), title);
        }
    }

    public record CommitEditRequest(
            String comment,
            String versionToken,
            String content
    ) {
    }


    @PutMapping("/{title}/edit")
    public WikiPageDataForRead commitEdit(
            @PathVariable String title,
            @ContributorInfo ContributorInfoArg contributorInfo,
            @RequestBody CommitEditRequest commitEditRequest
    ) {
        wikiPageCommandService.commitUpdate(contributorInfo.contributorId(), title, commitEditRequest.comment(), commitEditRequest.versionToken(), commitEditRequest.content());
        return wikiPageQueryService.getWikiPage(title);
    }
}
