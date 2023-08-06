package dev.prvt.yawiki.core.wikipage.domain.exception;

public class WikiPageUpdaterException extends WikiPageException {
    public WikiPageUpdaterException() {
        super();
    }

    public WikiPageUpdaterException(String message) {
        super(message);
    }

    public WikiPageUpdaterException(String message, Throwable cause) {
        super(message, cause);
    }

    public WikiPageUpdaterException(Throwable cause) {
        super(cause);
    }
}
