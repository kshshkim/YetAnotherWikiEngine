package dev.prvt.yawiki.core.member.domain;

public interface AuthenticationTokenGenerator {
    String create(BaseMember member);
}
