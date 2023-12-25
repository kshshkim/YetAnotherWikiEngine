package dev.prvt.yawiki.web.api.v1.wiki;

import dev.prvt.yawiki.core.wikipage.application.WikiPageCommandService;
import dev.prvt.yawiki.core.wikipage.application.WikiPageQueryService;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.web.markdown.ReferencedTitleExtractor;
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
    private final ReferencedTitleExtractor referencedTitleExtractor;

    @GetMapping("/{title}")
    public WikiPageDataForRead getWikiPage(
            @PathVariable WikiPageTitle title,
            @RequestParam(required = false) Integer rev
    ) {
        return rev == null ? wikiPageQueryService.getWikiPageDataForRead(title):
                wikiPageQueryService.getWikiPageDataForRead(title, rev);
    }

    @GetMapping("/{title}/history")
    public Page<RevisionData> getWikiHistory(
            @PathVariable WikiPageTitle title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return wikiPageQueryService.getRevisionHistory(
                title,
                Pageable.ofSize(size).withPage(page)
        );
    }

    @GetMapping("/{title}/edit")
    public WikiPageDataForUpdate proclaimEdit(
            @PathVariable WikiPageTitle title,
            @ContributorInfo ContributorInfoArg contributorInfo
    ) {
        try {
            return wikiPageCommandService.proclaimUpdate(contributorInfo.contributorId(), title);
        } catch (NoSuchWikiPageException e) {
            wikiPageCommandService.create(
                    contributorInfo.contributorId(),
                    title
            );
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
            @PathVariable WikiPageTitle title,
            @ContributorInfo ContributorInfoArg contributorInfo,
            @RequestBody CommitEditRequest commitEditRequest
    ) {
        wikiPageCommandService.commitUpdate(
                contributorInfo.contributorId(),
                title,
                commitEditRequest.comment(),
                commitEditRequest.versionToken(),
                commitEditRequest.content(),
                referencedTitleExtractor.extractReferencedTitles(commitEditRequest.content())
        );
        return wikiPageQueryService.getWikiPageDataForRead(title);
    }

    public record DeleteRequest(
            String comment,
            String versionToken
    ) {
    }


    @DeleteMapping("/{title}/edit")
    public void delete(
            @PathVariable WikiPageTitle title,
            @ContributorInfo ContributorInfoArg contributorInfo,
            @RequestBody DeleteRequest deleteRequest
    ) {
        wikiPageCommandService.delete(contributorInfo.contributorId(), title, deleteRequest.comment(), deleteRequest.versionToken());
    }
}
