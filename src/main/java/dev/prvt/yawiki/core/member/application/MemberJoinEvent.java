package dev.prvt.yawiki.core.member.application;

import java.util.UUID;

public record MemberJoinEvent(UUID memberId, String displayedName) {
}
