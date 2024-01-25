package dev.prvt.yawiki.core.wikipage.domain.exception;

public class WikiPageReferenceUpdaterException extends WikiPageException {
    public WikiPageReferenceUpdaterException() {
        super();
    }

    public WikiPageReferenceUpdaterException(String message) {
        super(message);
    }

    public WikiPageReferenceUpdaterException(String message, Throwable cause) {
        super(message, cause);
    }

    public WikiPageReferenceUpdaterException(Throwable cause) {
        super(cause);
    }
}
