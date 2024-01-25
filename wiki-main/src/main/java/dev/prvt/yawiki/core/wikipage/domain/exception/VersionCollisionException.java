package dev.prvt.yawiki.core.wikipage.domain.exception;

public class VersionCollisionException extends UpdateValidationException {
    public VersionCollisionException() {
        super();
    }

    public VersionCollisionException(String message) {
        super(message);
    }

    public VersionCollisionException(String message, Throwable cause) {
        super(message, cause);
    }

    public VersionCollisionException(Throwable cause) {
        super(cause);
    }
}
