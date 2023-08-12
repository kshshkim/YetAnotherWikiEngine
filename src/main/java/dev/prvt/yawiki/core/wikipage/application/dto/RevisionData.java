package dev.prvt.yawiki.core.wikipage.application.dto;

import com.querydsl.core.annotations.QueryProjection;

public record RevisionData(
        int revVersion,
        int diff,
        String contributorName,
        String comment
) {
    @QueryProjection
    public RevisionData {
    }
}
