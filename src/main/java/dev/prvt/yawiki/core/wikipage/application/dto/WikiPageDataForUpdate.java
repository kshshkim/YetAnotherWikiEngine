package dev.prvt.yawiki.core.wikipage.application.dto;


public record WikiPageDataForUpdate(
        String title,
        String content,
        String versionToken
) {
}
