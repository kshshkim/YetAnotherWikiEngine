package dev.prvt.yawiki.application.domain.wikipage.exception;

public class EditTokenMismatchException extends EditValidationException {
    public EditTokenMismatchException() {
        super();
    }

    public EditTokenMismatchException(String message) {
        super(message);
    }

    public EditTokenMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public EditTokenMismatchException(Throwable cause) {
        super(cause);
    }
}
