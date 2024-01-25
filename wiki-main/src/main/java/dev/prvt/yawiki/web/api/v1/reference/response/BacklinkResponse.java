package dev.prvt.yawiki.web.api.v1.reference.response;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.web.api.v1.common.response.PageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public record BacklinkResponse(
        WikiPageTitle referredTitle,
        List<WikiPageTitle> referrers,
        PageInfo pageInfo
) {
    public static BacklinkResponse from(WikiPageTitle referred, Page<WikiPageTitle> referrers) {
        return new BacklinkResponse(referred, referrers.getContent(), PageInfo.from(referrers));
    }
}
