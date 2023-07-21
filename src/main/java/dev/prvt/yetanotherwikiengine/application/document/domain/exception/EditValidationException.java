package dev.prvt.yetanotherwikiengine.application.document.domain.exception;

public class EditValidationException extends RuntimeException {
    public EditValidationException() {
        super();
    }

    public EditValidationException(String message) {
        super(message);
    }

    public EditValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EditValidationException(Throwable cause) {
        super(cause);
    }
}
