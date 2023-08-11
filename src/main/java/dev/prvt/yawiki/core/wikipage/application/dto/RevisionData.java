package dev.prvt.yawiki.core.wikipage.application.dto;

public record RevisionData(
        long revVersion,
        int diff,
        String contributorName,
        String comment
) {}
