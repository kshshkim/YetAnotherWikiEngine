package dev.prvt.yawiki.core.wikipage.domain.exception;

public class WikiPageException extends RuntimeException {
    public WikiPageException() {
        super();
    }

    public WikiPageException(String message) {
        super(message);
    }

    public WikiPageException(String message, Throwable cause) {
        super(message, cause);
    }

    public WikiPageException(Throwable cause) {
        super(cause);
    }
}
