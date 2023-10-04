package dev.prvt.yawiki.auth.member.domain;

public interface AuthenticationTokenGenerator {
    String create(BaseMember member);
}
