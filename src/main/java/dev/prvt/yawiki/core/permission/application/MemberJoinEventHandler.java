package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.event.MemberJoinEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * MemberJoinEvent 를 받아 AuthorityProfile 을 생성함.
 */
@Component
@Transactional
@RequiredArgsConstructor
public class MemberJoinEventHandler {
    private final AuthorityProfileCommandService authorityProfileCommandService;

    @EventListener
    public void handle(MemberJoinEvent memberJoinEvent) {
        authorityProfileCommandService.createAuthorityProfile(memberJoinEvent.memberId());
    }
}
