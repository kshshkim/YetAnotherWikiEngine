package dev.prvt.yawiki.web.api.v1.reference;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikireference.application.WikiReferenceQueryService;
import dev.prvt.yawiki.web.api.v1.reference.response.BacklinkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reference")
public class ReferenceController {

    private final WikiReferenceQueryService wikiReferenceQueryService;

    /**
     * 문서 제목에 대한 역링크 조회
     * @param referredTitle 역링크를 조회할 문서 제목
     * @param pageable pageable
     * @return {@link BacklinkResponse}
     */
    @GetMapping("/backlink/{referredTitle}")
    public BacklinkResponse getBackLinks(
            @PathVariable WikiPageTitle referredTitle,
            @PageableDefault(size = 50, page = 0) Pageable pageable
    ) {
        Page<WikiPageTitle> backlinks = wikiReferenceQueryService.getBacklinks(referredTitle, pageable);
        return BacklinkResponse.from(referredTitle, backlinks);
    }
}
