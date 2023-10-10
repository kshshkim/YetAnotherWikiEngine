package dev.prvt.yawiki.auth.jwt.domain;

import java.util.UUID;

public interface AccessTokenGenerator {
    String generate(UUID contributorId, String contributorName);
}
