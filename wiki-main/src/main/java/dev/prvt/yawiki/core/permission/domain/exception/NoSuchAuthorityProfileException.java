package dev.prvt.yawiki.core.permission.domain.exception;

import java.util.UUID;

public class NoSuchAuthorityProfileException extends RuntimeException {
    public NoSuchAuthorityProfileException(String message) {
        super(message);
    }

    public NoSuchAuthorityProfileException(UUID nonExistId) {
        this("No such AuthorityProfile. ID: " + nonExistId);
    }
}
