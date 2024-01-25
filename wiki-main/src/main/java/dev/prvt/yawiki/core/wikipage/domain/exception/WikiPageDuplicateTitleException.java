package dev.prvt.yawiki.core.wikipage.domain.exception;

import dev.prvt.yawiki.common.model.WikiPageTitle;

public class WikiPageDuplicateTitleException extends RuntimeException {
    public WikiPageDuplicateTitleException(String message) {
        super(message);
    }

    public WikiPageDuplicateTitleException(WikiPageTitle wikiPageTitle) {
        this("title already exists. " + wikiPageTitle);
    }
}
