package dev.prvt.yawiki.core.wikipage.domain.exception;

public class NoSuchWikiPageException extends WikiPageException {
    public NoSuchWikiPageException() {
        super();
    }

    public NoSuchWikiPageException(String message) {
        super(message);
    }

    public NoSuchWikiPageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchWikiPageException(Throwable cause) {
        super(cause);
    }
}
