package dev.prvt.yawiki.auth.jwt.domain;

public class RefreshTokenException extends RuntimeException {

    public static RefreshTokenException EXPIRED = new RefreshTokenException("refresh token expired");
    public static RefreshTokenException NOT_FOUND = new RefreshTokenException("refresh token not found");
    public static RefreshTokenException TOKEN_SUB_MISMATCH = new RefreshTokenException("refresh token sub name mismatch");

    public RefreshTokenException() {
        super();
    }

    public RefreshTokenException(String message) {
        super(message);
    }

    public RefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenException(Throwable cause) {
        super(cause);
    }
}
