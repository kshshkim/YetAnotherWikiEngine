package dev.prvt.yawiki.core.event;

import java.util.UUID;

public record MemberJoinEvent(UUID memberId, String displayedName) {
}
