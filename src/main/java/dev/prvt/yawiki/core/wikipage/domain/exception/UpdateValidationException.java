package dev.prvt.yawiki.core.wikipage.domain.exception;

public class UpdateValidationException extends RuntimeException {
    public UpdateValidationException() {
        super();
    }

    public UpdateValidationException(String message) {
        super(message);
    }

    public UpdateValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateValidationException(Throwable cause) {
        super(cause);
    }
}
