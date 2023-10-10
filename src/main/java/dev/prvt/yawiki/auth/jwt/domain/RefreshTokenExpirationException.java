package dev.prvt.yawiki.auth.jwt.domain;

public class RefreshTokenExpirationException extends RuntimeException {
    public static RefreshTokenExpirationException INSTANCE = new RefreshTokenExpirationException();
}
