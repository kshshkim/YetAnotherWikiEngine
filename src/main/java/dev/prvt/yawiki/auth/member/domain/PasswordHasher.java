package dev.prvt.yawiki.auth.member.domain;

public interface PasswordHasher {
    String hash(String toHash);
    boolean matches(String raw, String hashed);
}
