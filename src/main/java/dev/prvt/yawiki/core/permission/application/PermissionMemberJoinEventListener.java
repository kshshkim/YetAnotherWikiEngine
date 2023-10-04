package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.event.MemberJoinEvent;
import dev.prvt.yawiki.core.permission.domain.PermissionMemberJoinEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class PermissionMemberJoinEventListener {
    private final PermissionMemberJoinEventHandler permissionMemberJoinEventHandler;
    @EventListener
    @Transactional
    public void handleEvent(MemberJoinEvent memberJoinEvent) {
        permissionMemberJoinEventHandler.handle(memberJoinEvent);
    }
}
