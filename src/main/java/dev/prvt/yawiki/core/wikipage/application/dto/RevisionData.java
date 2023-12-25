package dev.prvt.yawiki.core.wikipage.application.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.util.UUID;

public record RevisionData(
        int revVersion,
        int diff,
        UUID contributorId,
        String comment
) {
    @QueryProjection
    public RevisionData {
    }
}
