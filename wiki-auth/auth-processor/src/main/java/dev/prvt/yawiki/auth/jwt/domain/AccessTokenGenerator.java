package dev.prvt.yawiki.auth.jwt.domain;

public interface AccessTokenGenerator {

    String generate(TokenPayload payload);

}
