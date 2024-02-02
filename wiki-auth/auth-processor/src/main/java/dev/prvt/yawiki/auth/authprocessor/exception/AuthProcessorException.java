package dev.prvt.yawiki.auth.authprocessor.exception;

public class AuthProcessorException extends RuntimeException {

    public AuthProcessorException() {
        super();
    }

    public AuthProcessorException(String message) {
        super(message);
    }

    public AuthProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthProcessorException(Throwable cause) {
        super(cause);
    }

}
