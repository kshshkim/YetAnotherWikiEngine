package dev.prvt.yawiki.member.application;

import java.util.UUID;

public record MemberData(
    UUID id,
    String username
) {

}
