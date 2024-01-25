package dev.prvt.yawiki.core.wikipage.domain.exception;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;

public class WikiPageRenameException extends WikiPageException {

    public static final WikiPageRenameException WIKI_PAGE_IS_NOT_ACTIVE = new WikiPageRenameException("Cannot rename inactive WikiPage.");

    public WikiPageRenameException(String message) {
        super(message);
    }

    public static WikiPageRenameException notActive(WikiPage wikiPage) {
        return WIKI_PAGE_IS_NOT_ACTIVE;
    }
}
