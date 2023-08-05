package dev.prvt.yawiki.core.wikipage.domain.exception;

public class UpdatePermissionException extends UpdateValidationException {
    public UpdatePermissionException() {
        super();
    }

    public UpdatePermissionException(String message) {
        super(message);
    }

    public UpdatePermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdatePermissionException(Throwable cause) {
        super(cause);
    }
}
