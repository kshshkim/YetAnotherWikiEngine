package dev.prvt.yawiki.web.api.v1.wiki;

import dev.prvt.yawiki.core.wikipage.application.WikiPageCommandService;
import dev.prvt.yawiki.core.wikipage.application.WikiPageQueryService;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
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
    private final NamespaceParser namespaceParser;

    @GetMapping("/{title}")
    public WikiPageDataForRead getWikiPage(
            @PathVariable String title,
            @RequestParam(required = false) Integer rev
    ) {
        Namespace namespace = namespaceParser.parseTitle(title);
        WikiPageTitle wikiPageTitle = new WikiPageTitle(title, namespace);
        return rev == null ? wikiPageQueryService.getWikiPage(wikiPageTitle):
                wikiPageQueryService.getWikiPage(wikiPageTitle, rev);
    }

    @GetMapping("/{title}/history")
    public Page<RevisionData> getWikiHistory(
            @PathVariable String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Namespace namespace = namespaceParser.parseTitle(title);
        WikiPageTitle wikiPageTitle = new WikiPageTitle(title, namespace);
        return wikiPageQueryService.getRevisionHistory(
                wikiPageTitle,
                Pageable.ofSize(size).withPage(page)
        );
    }

    @GetMapping("/{title}/edit")
    public WikiPageDataForUpdate proclaimEdit(
            @PathVariable String title,
            @ContributorInfo ContributorInfoArg contributorInfo
    ) {
        WikiPageTitle wikiPageTitle = new WikiPageTitle(title, namespaceParser.parseTitle(title));

        try {
            return wikiPageCommandService.proclaimUpdate(contributorInfo.contributorId(), wikiPageTitle);
        } catch (NoSuchWikiPageException e) {
            wikiPageCommandService.create(
                    contributorInfo.contributorId(),
                    wikiPageTitle
            );
            return wikiPageCommandService.proclaimUpdate(contributorInfo.contributorId(), wikiPageTitle);
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
        Namespace namespace = namespaceParser.parseTitle(title);

        WikiPageTitle wikiPageTitle = new WikiPageTitle(title, namespace);
        wikiPageCommandService.commitUpdate(contributorInfo.contributorId(), wikiPageTitle, commitEditRequest.comment(), commitEditRequest.versionToken(), commitEditRequest.content());
        return wikiPageQueryService.getWikiPage(wikiPageTitle);
    }

    public record DeleteRequest(
            String comment,
            String versionToken
    ) {
    }


    @DeleteMapping("/{title}/edit")
    public void delete(
            @PathVariable String title,
            @ContributorInfo ContributorInfoArg contributorInfo,
            @RequestBody DeleteRequest deleteRequest
    ) {
        Namespace namespace = namespaceParser.parseTitle(title);
        WikiPageTitle wikiPageTitle = new WikiPageTitle(title, namespace);
        wikiPageCommandService.delete(contributorInfo.contributorId(), wikiPageTitle, deleteRequest.comment(), deleteRequest.versionToken());
    }
}
