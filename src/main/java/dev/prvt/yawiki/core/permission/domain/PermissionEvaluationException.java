package dev.prvt.yawiki.core.permission.domain;

public class PermissionEvaluationException extends RuntimeException {
    public PermissionEvaluationException() {
        super();
    }

    public PermissionEvaluationException(String message) {
        super(message);
    }

    public PermissionEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionEvaluationException(Throwable cause) {
        super(cause);
    }

    protected PermissionEvaluationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
